$ErrorActionPreference = 'Stop'

# =========================
# CONFIG
# =========================
$friendBase = "http://localhost:8083/api/friends"
$authBase   = "http://localhost:8082/auth"   # <-- adapte si besoin

# Creds de test (ces comptes doivent exister dans ms-auth)
$user1Creds = @{ username = "alice"; password = "alicepwd" } | ConvertTo-Json
$user2Creds = @{ username = "bob";   password = "bobpwd"   } | ConvertTo-Json

# =========================
# HELPERS
# =========================

function Get-TokenFromLoginResponse {
param($loginResp, $loginHeaders)

    # 1) Essaye des propriétés courantes
    $candidates = @(
        "token","jwt","accessToken","access_token","id_token"
    )
    foreach ($k in $candidates) {
        if ($loginResp.PSObject.Properties.Name -contains $k) {
            $v = $loginResp.$k
            if (-not [string]::IsNullOrWhiteSpace($v)) { return $v }
        }
    }

    # 2) Parfois le token arrive dans l'entête Authorization: Bearer xxx
    if ($loginHeaders -and $loginHeaders.Authorization) {
        $auth = $loginHeaders.Authorization
        if ($auth -is [System.Array]) { $auth = $auth[0] }
        if ($auth -match "^Bearer\s+(.+)$") { return $Matches[1] }
    }

    throw "Impossible de trouver le token dans la réponse de login (essayé: token/jwt/accessToken/access_token/id_token ou header Authorization)."
}

function Invoke-AuthRest {
param([string]$Method="GET",[string]$Url,[string]$Token,$Body=$null)
$headers = @{ "Authorization" = "Bearer $Token" }
if ($Body -ne $null) {
Invoke-RestMethod $Url -Method $Method -Headers $headers -ContentType "application/json" -Body $Body
} else {
Invoke-RestMethod $Url -Method $Method -Headers $headers
}
}

function Invoke-AuthWebRequest {
param([string]$Method="GET",[string]$Url,[string]$Token)
$headers = @{ "Authorization" = "Bearer $Token" }
Invoke-WebRequest $Url -Method $Method -Headers $headers
}

# =========================
# 0) Sanity (health + ping public)
# =========================
Invoke-RestMethod "http://localhost:8083/actuator/health" | Out-Null
(Invoke-RestMethod "$friendBase/ping") | Out-Null

# =========================
# 1) Login ms-auth pour récupérer les JWT
# =========================
# On utilise Invoke-WebRequest pour capturer aussi les entêtes (si le token est dans Authorization)
try {
$resp1 = Invoke-WebRequest "$authBase/login" -Method POST -ContentType "application/json" -Body $user1Creds -ErrorAction Stop
} catch {
$r = $_.Exception.Response
if ($r) {
$sr = New-Object IO.StreamReader($r.GetResponseStream())
$body = $sr.ReadToEnd()
"LOGIN user1 -> HTTP $([int]$r.StatusCode)"
$body
}
throw
}
try {
$resp2 = Invoke-WebRequest "$authBase/login" -Method POST -ContentType "application/json" -Body $user2Creds -ErrorAction Stop
} catch {
$r = $_.Exception.Response
if ($r) {
$sr = New-Object IO.StreamReader($r.GetResponseStream())
$body = $sr.ReadToEnd()
"LOGIN user2 -> HTTP $([int]$r.StatusCode)"
$body
}
throw
}


$login1 = $null
$login2 = $null
try   { $login1 = $resp1.Content | ConvertFrom-Json } catch {}
try   { $login2 = $resp2.Content | ConvertFrom-Json } catch {}

$tokenUser1 = Get-TokenFromLoginResponse -loginResp $login1 -loginHeaders $resp1.Headers
$tokenUser2 = Get-TokenFromLoginResponse -loginResp $login2 -loginHeaders $resp2.Headers

# =========================
# 1.b) Déterminer targetUserId (id de user2)
# =========================
# Plusieurs APIs renvoient l'id dans la réponse du login (ex: { user: { id: 2 } } ou { id: 2 }).
# On tente de l'extraire; sinon, mets une valeur en dur (ex: 2).
function TryGetUserId {
param($obj)
if ($null -eq $obj) { return $null }
if ($obj.PSObject.Properties.Name -contains "userId") { return [int64]$obj.userId }
if ($obj.PSObject.Properties.Name -contains "id")     { return [int64]$obj.id }
if ($obj.PSObject.Properties.Name -contains "user") {
$u = $obj.user
if ($u -and $u.PSObject.Properties.Name -contains "id") { return [int64]$u.id }
if ($u -and $u.PSObject.Properties.Name -contains "userId") { return [int64]$u.userId }
}
return $null
}

$targetUserId = TryGetUserId -obj $login2
if (-not $targetUserId) {
# 🔧 fallback: mets ici l'id de bob si ton /auth/login ne le renvoie pas
$targetUserId = 2
}

# =========================
# 2) User1 -> demande d’ami vers User2
# =========================
$reqBody = @{ targetUserId = $targetUserId } | ConvertTo-Json
$f1 = Invoke-AuthRest -Method POST -Url $friendBase -Token $tokenUser1 -Body $reqBody
"REQUEST -> 200 attendu"; $f1 | ConvertTo-Json -Depth 5

# =========================
# 3) Relancer la même demande (doit rester PENDING si non acceptée)
# =========================
$f2 = Invoke-AuthRest -Method POST -Url $friendBase -Token $tokenUser1 -Body $reqBody
"REQUEST again -> même relation (PENDING relancée)"; $f2 | ConvertTo-Json -Depth 5

# =========================
# 4) User2 -> accepte (POST /api/friends/accept/{targetUserId})
# =========================
$acceptUrl = "$friendBase/accept/$targetUserId"
$f3 = Invoke-AuthRest -Method POST -Url $acceptUrl -Token $tokenUser2
"ACCEPT -> 200 attendu (ACCEPTED)"; $f3 | ConvertTo-Json -Depth 5

# =========================
# 5) Listes ACCEPTED (user1 puis user2)
# =========================
$l1 = Invoke-AuthRest -Method GET -Url $friendBase -Token $tokenUser1
"LIST (user1) -> doit contenir user2"; $l1 | ConvertTo-Json -Depth 5

$l2 = Invoke-AuthRest -Method GET -Url $friendBase -Token $tokenUser2
"LIST (user2) -> doit contenir user1"; $l2 | ConvertTo-Json -Depth 5

# =========================
# 6) Suppression (user1) - DELETE /api/friends/{targetUserId}
# =========================
$delUrl = "$friendBase/$targetUserId"
$del = Invoke-AuthWebRequest -Method DELETE -Url $delUrl -Token $tokenUser1
"DELETE -> StatusCode 204 attendu : $($del.StatusCode)"

# =========================
# 7) Vérif après suppression
# =========================
$l1b = Invoke-AuthRest -Method GET -Url $friendBase -Token $tokenUser1
"LIST after delete (user1) -> ne doit pas contenir user2"; $l1b | ConvertTo-Json -Depth 5

# === 0) Sanity check
Invoke-RestMethod "http://localhost:8088/actuator/health"

# === 1) Create
$create = @{
nom = "PUBG Squad"
date = "2025-10-02"
heure = "20:30:00"
jeu = "PUBG"
statut = "OUVERT"
slots = 3
organisateurId = 1
} | ConvertTo-Json

$r1 = Invoke-RestMethod "http://localhost:8088/rdv" -Method POST -ContentType "application/json" -Body $create
"CREATE -> HTTP 201 attendu"; $r1 | ConvertTo-Json -Depth 5
$id = $r1.id

# === 2) Get by id
$r2 = Invoke-RestMethod "http://localhost:8088/rdv/$id"
"GET BY ID -> HTTP 200"; $r2 | ConvertTo-Json -Depth 5

# === 3) List par jour
$r3 = Invoke-RestMethod "http://localhost:8088/rdv?date=2025-10-02"
"LIST BY DATE -> HTTP 200"; $r3 | ConvertTo-Json -Depth 5

# === 4) Update
$update = @{
nom = "PUBG Squad+"
date = "2025-10-02"
heure = "21:00:00"
jeu = "PUBG"
statut = "OUVERT"
slots = 4
} | ConvertTo-Json

$r4 = Invoke-RestMethod "http://localhost:8088/rdv/$id" -Method PUT -ContentType "application/json" -Body $update
"UPDATE -> HTTP 200"; $r4 | ConvertTo-Json -Depth 5

# === 5) Demande de participation
$partReq = @{ userId = 42; role = "JOUEUR" } | ConvertTo-Json
$p1 = Invoke-RestMethod "http://localhost:8088/rdv/$id/participations" -Method POST -ContentType "application/json" -Body $partReq
"PARTICIPATION CREATE -> HTTP 201"; $p1 | ConvertTo-Json -Depth 5
$pid = $p1.id

# === 6) Confirmer la participation (gère le 409 capacité atteinte)
$confirm = @{ status = "CONFIRME" } | ConvertTo-Json
try {
$p2 = Invoke-RestMethod "http://localhost:8088/rdv/$id/participations/$pid/status" -Method PATCH -ContentType "application/json" -Body $confirm
"PARTICIPATION CONFIRM -> HTTP 200"; $p2 | ConvertTo-Json -Depth 5
} catch {
$resp = $_.Exception.Response
if ($resp) {
$reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
$body = $reader.ReadToEnd()
$code = [int]$resp.StatusCode
"PARTICIPATION CONFIRM -> HTTP $code (attendu 409 si capacité atteinte)"
$body
} else {
throw
}
}

# === 7) Lister les participants
$rList = Invoke-RestMethod "http://localhost:8088/rdv/$id/participations"
"PARTICIPATION LIST -> HTTP 200"; $rList | ConvertTo-Json -Depth 5

# === 8) Annuler le RDV
$rCancel = Invoke-RestMethod "http://localhost:8088/rdv/$id/cancel" -Method POST
"CANCEL RDV -> HTTP 200"; $rCancel | ConvertTo-Json -Depth 5

# (facultatif) Tente un update après annulation (attendu: 409)
try {
Invoke-RestMethod "http://localhost:8088/rdv/$id" -Method PUT -ContentType "application/json" -Body $update
} catch {
$resp = $_.Exception.Response
if ($resp) {
$reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
$body = $reader.ReadToEnd()
$code = [int]$resp.StatusCode
"UPDATE after cancel -> HTTP $code (attendu 409)"; $body
}
}

# === 9) Delete (statut exact)
$del = Invoke-WebRequest "http://localhost:8088/rdv/$id" -Method DELETE
"DELETE -> StatusCode attendu 204 : $($del.StatusCode)"











$ErrorActionPreference = 'Stop'

# 0) Health
(Invoke-RestMethod "http://localhost:8088/actuator/health").status

# 1) Create
$create = @{
nom = "PUBG Squad"
date = "2025-10-02"
heure = "20:30:00"
jeu = "PUBG"
statut = "OUVERT"
slots = 3
organisateurId = 1
} | ConvertTo-Json

$r1 = Invoke-RestMethod "http://localhost:8088/rdv" -Method POST -ContentType "application/json" -Body $create
"CREATE -> attendu 201"; $r1 | ConvertTo-Json -Depth 5
$id = $r1.id

# 2) Get by id
$r2 = Invoke-RestMethod "http://localhost:8088/rdv/$id"
"GET BY ID -> 200"; $r2 | ConvertTo-Json -Depth 5

# 3) List par jour
$r3 = Invoke-RestMethod "http://localhost:8088/rdv?date=2025-10-02"
"LIST BY DATE -> 200"; $r3 | ConvertTo-Json -Depth 5

# 4) Update
$update = @{
nom = "PUBG Squad+"
date = "2025-10-02"
heure = "21:00:00"
jeu = "PUBG"
statut = "OUVERT"
slots = 4
} | ConvertTo-Json

$r4 = Invoke-RestMethod "http://localhost:8088/rdv/$id" -Method PUT -ContentType "application/json" -Body $update
"UPDATE -> 200"; $r4 | ConvertTo-Json -Depth 5

# 5) Demande de participation
$partReq = @{ userId = 42; role = "JOUEUR" } | ConvertTo-Json
$p1 = Invoke-RestMethod "http://localhost:8088/rdv/$id/participations" -Method POST -ContentType "application/json" -Body $partReq
"PARTICIPATION CREATE -> 201"; $p1 | ConvertTo-Json -Depth 5
$participantId = $p1.id   # 👈 NE PAS utiliser $pid

# 6) Confirmer la participation (gère 409)
$confirm = @{ status = "CONFIRME" } | ConvertTo-Json
try {
$p2 = Invoke-RestMethod "http://localhost:8088/rdv/$id/participations/$participantId/status" -Method PATCH -ContentType "application/json" -Body $confirm
"PARTICIPATION CONFIRM -> 200"; $p2 | ConvertTo-Json -Depth 5
} catch {
$resp = $_.Exception.Response
if ($resp) {
$reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
$body = $reader.ReadToEnd()
$code = [int]$resp.StatusCode
"PARTICIPATION CONFIRM -> $code (attendu 409 si capacité atteinte)"
$body
} else { throw }
}

# 7) Lister les participants
$rList = Invoke-RestMethod "http://localhost:8088/rdv/$id/participations"
"PARTICIPATION LIST -> 200"; $rList | ConvertTo-Json -Depth 5

# 8) Annuler le RDV
$rCancel = Invoke-RestMethod "http://localhost:8088/rdv/$id/cancel" -Method POST
"CANCEL RDV -> 200"; $rCancel | ConvertTo-Json -Depth 5

# (facultatif) Update après annulation = 409 attendu
try {
Invoke-RestMethod "http://localhost:8088/rdv/$id" -Method PUT -ContentType "application/json" -Body $update
} catch {
$resp = $_.Exception.Response
if ($resp) {
$reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
$body = $reader.ReadToEnd()
$code = [int]$resp.StatusCode
"UPDATE after cancel -> $code (attendu 409)"; $body
}
}

# 9) Delete
$del = Invoke-WebRequest "http://localhost:8088/rdv/$id" -Method DELETE
"DELETE -> StatusCode attendu 204 : $($del.StatusCode)"

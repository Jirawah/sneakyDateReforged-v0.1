| Fichier | Méthodes publiques à tester |
|---------|------------------------------|
| `ms_auth/bot/DiscordBot.java` | start, shutdown |
| `ms_auth/bot/VoiceChannelListener.java` | onGuildVoiceUpdate, onFailure, onResponse |
| `ms_auth/config/DiscordBotConfig.java` | run |
| `ms_auth/config/SecurityConfig.java` | authenticationProvider, passwordEncoder |
| `ms_auth/controller/AuthController.java` | register, login, syncDiscord |
| `ms_auth/controller/DiscordController.java` | syncDiscord |
| `ms_auth/controller/PasswordResetController.java` | requestReset, resetPassword |
| `ms_auth/procedure/RegisterProcedureExecutor.java` | execute |
| `ms_auth/security/JwtUtils.java` | init, generateToken, extractUsername, isTokenValid |
| `ms_auth/service/AuthService.java` | register, login, syncDiscord |
| `ms_auth/service/DiscordSyncService.java` | handleSync |
| `ms_auth/service/PasswordResetService.java` | requestReset, resetPassword |
| `ms_auth/service/SteamVerificationService.java` | verifySteamUser |
| `ms_auth/service/UserAuthService.java` | syncDiscordProfile, updateSteamProfile |

package com.sneakyDateReforged.ms_auth.procedure;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RegisterProcedureExecutor {

    private final JdbcTemplate jdbcTemplate;

    public int execute(String email, String pseudo, String passwordHash, String steamId, String discordId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_register_user")
                .declareParameters(
                        new SqlParameter("p_email", Types.VARCHAR),
                        new SqlParameter("p_pseudo", Types.VARCHAR),
                        new SqlParameter("p_password", Types.VARCHAR),
                        new SqlParameter("p_steam_id", Types.VARCHAR),
                        new SqlParameter("p_discord_id", Types.VARCHAR),
                        new SqlOutParameter("p_result_code", Types.INTEGER)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_email", email);
        inParams.put("p_pseudo", pseudo);
        inParams.put("p_password", passwordHash);
        inParams.put("p_steam_id", steamId);
        inParams.put("p_discord_id", discordId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            return (Integer) result.get("p_result_code");
        } catch (DataAccessException ex) {
            throw new RuntimeException("Erreur lors de l’enregistrement via la procédure stockée", ex);
        }
    }
}

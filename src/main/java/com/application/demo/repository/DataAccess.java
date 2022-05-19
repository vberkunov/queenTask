package com.application.demo.repository;

import com.application.demo.entity.QueenFile;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class DataAccess {

    final String INSERT_QUERY = "insert into queen (data, name,type) values (?, ?, ?)";
    JdbcTemplate jdbcTemplate;

    public DataAccess(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(QueenFile queenFile) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setBytes(1, queenFile.getData());
            ps.setString(2, queenFile.getName());
            ps.setString(3, queenFile.getType());
            return ps;
        }, keyHolder);
        long newId;
        if (keyHolder.getKeys().size() > 1) {
            newId = (long)keyHolder.getKeys().get("id");
        } else {
            newId= keyHolder.getKey().longValue();
        }
        return newId;
    }

    public QueenFile getFileById(long id) {

        String sql = "SELECT * FROM queen WHERE ID = ?";

        return (QueenFile) jdbcTemplate.queryForObject(
                sql,
                new Object[]{id},
                new BeanPropertyRowMapper(QueenFile.class));
    }
}

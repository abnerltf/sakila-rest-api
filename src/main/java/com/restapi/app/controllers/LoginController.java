package com.restapi.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Controller
public class LoginController
{
	private MysqlDataSource dataSource;

	public LoginController()
	{
		dataSource = new MysqlDataSource();
		dataSource.setServerName("localhost");
		dataSource.setPortNumber(3306);
		dataSource.setDatabaseName("sakila");
		dataSource.setUser("root");
		dataSource.setPassword("root");
	}

	@PostMapping(value="/api/v1/login", produces="application/json")
	@ResponseBody
	public String validate(HttpServletRequest request) throws SQLException
	{
		if(request.getParameterMap().size() <= 0)
		{
			return Json.createObjectBuilder().add("error", "No parameters found").build().toString();
		}

		else if(request.getParameterMap().containsKey("login") && 
			request.getParameterMap().containsKey("pass"))
		{
			Connection conn = this.dataSource.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM admin "+
																"WHERE login = ? AND "+
																"pass = ?");
			statement.setString(1, request.getParameter("login"));
			statement.setString(2, request.getParameter("pass"));
			statement.execute();

			ResultSet result = statement.getResultSet();
			result.last(); //Move o cursor ao último resultado

			if(result.getRow() == 1)
			{
				HttpSession session = request.getSession();
				session.setAttribute("authenticated", true);
				return Json.createObjectBuilder().add("message", "Authentication successful").build().toString();
			}
			else if(result.getRow() == 0)
			{
				return Json.createObjectBuilder().add("message", "Authentication failed").build().toString();
			}
			else if(result.getRow() > 1)
			{
				HttpSession session = request.getSession();
				session.setAttribute("authenticated", true);
				return Json.createObjectBuilder().add("message", "Authentication successful").build().toString();
			}
		}

		return Json.createObjectBuilder().add("error", "Invalid request").build().toString();
	}
}
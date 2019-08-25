package com.restapi.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller
public class CustomerController
{
	private MysqlDataSource dataSource;

	public CustomerController()
	{
		this.dataSource = new MysqlDataSource();
		this.dataSource.setServerName("localhost");
		this.dataSource.setPortNumber(3306);
		this.dataSource.setDatabaseName("sakila");
		this.dataSource.setUser("root");
		this.dataSource.setPassword("root");
	}

	@GetMapping(value="/api/v1/customer/{userId}", produces="application/json")
	@ResponseBody
	public String getUser(@PathVariable("userId") int userId) throws SQLException
	{
		JsonObject jsonResult = Json.createObjectBuilder().build();
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

		Connection conn = this.dataSource.getConnection();
		Statement statement = conn.createStatement();
		String query = "SELECT * FROM customer WHERE customer_id = "+String.valueOf(userId);
		ResultSet queryResult = statement.executeQuery(query);

		while(queryResult.next())
		{
			for(String column : this.getColumns(queryResult))
			{
				jsonBuilder.add(column, queryResult.getString(column));
			}
		}

		jsonResult = jsonBuilder.build();
		return jsonResult.toString();
	}

	@GetMapping(value = "/api/v1/customers", produces = "application/json")
	@ResponseBody
	public String getUsers() throws SQLException
	{
		JsonObject jsonResult = Json.createObjectBuilder().build();
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

		Connection conn = this.dataSource.getConnection();
		Statement statement = conn.createStatement();
		ResultSet queryResult = statement.executeQuery("SELECT * FROM sakila.customer");

		while(queryResult.next())
		{
			JsonObjectBuilder customerInfo = Json.createObjectBuilder();
			for(String column : this.getColumns(queryResult))
			{
				customerInfo.add(column, queryResult.getString(column));
			}

			jsonBuilder.add(queryResult.getString("customer_id"), customerInfo.build());
		}

		jsonResult = jsonBuilder.build();
		return jsonResult.toString();
	}

	private ArrayList<String> getColumns(ResultSet queryResult) throws SQLException
	{
		ArrayList<String> columns = new ArrayList<String>();
		ResultSetMetaData metaData = queryResult.getMetaData();

		for(int i = 1; i <= metaData.getColumnCount(); i++)
		{
			columns.add(metaData.getColumnName(i));
		}
		return columns;
	}
}
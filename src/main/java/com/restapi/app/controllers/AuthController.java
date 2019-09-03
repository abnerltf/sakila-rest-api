package com.restapi.app.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AuthController
{
	public static boolean isAuthenticated(HttpServletRequest request)
	{
		if(hasSession(request))
		{
			HttpSession session = request.getSession();
			if(session.getAttribute("authenticated") == null)
			{
				return false;
			} 
			else
			{
				return true;
			}
		} 
		else
		{
			return false;
		}
	}

	public static boolean isAuthorized(HttpServletRequest request)
	{
		return true;
	}

	private static boolean hasSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);

		if(session == null)
		{
			request.getSession();
			return false;
		}

		return true;
	}
}
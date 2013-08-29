/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/** @author Yakoub
 */
package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.n52.sir.ds.IUserAccountDAO;
import org.n52.sir.util.ShortAlphanumericIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGSQLUserAccountDAO implements IUserAccountDAO {
	/**
	 * the logger, used to log exceptions and additionally information
	 */
	private static Logger log = LoggerFactory
			.getLogger(PGSQLUserAccountDAO.class);

	/**
	 * Connection pool for creating connections to the DB
	 */
	private PGConnectionPool cpool;
	
	public PGSQLUserAccountDAO(){
		
	}
	
	public PGSQLUserAccountDAO(PGConnectionPool cpool){
		this.cpool = cpool;
	}

	public String insertScript(String path, String username, int version) {
		String insert;
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			String insertQuery = insertScriptString(path, username, version);
			System.out.println(insertQuery);
			log.info(insertQuery);
			stmt.execute(insertQuery);
			String id = null;
			ResultSet rs = stmt.executeQuery(searchByPath(path));
			if(rs.next()){
				id = rs.getString(PGDAOConstants.SCRIPTID);
			}
			return id;
		} catch (Exception e) {
			log.error("Cannot insert harvest Script",e);
			return null;
		}
	}
	
	private String getPathById(String id) {
		String query;
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			String searchQuery = searchPathById(id);
			log.info(searchQuery);
			String path = null;
			ResultSet rs = stmt.executeQuery(searchQuery);
			String user = null;
			if(rs.next()){
				path = rs.getString(PGDAOConstants.PATH_URL);
				user = rs.getString(PGDAOConstants.SCRIPT_OWNER_USERNAME);
			}
			return user+"/"+path;
		} catch (Exception e) {
			log.error("Cannot search for harvest Script",e);
			return null;
		}
	}
	
	private String insertScriptString(String path,String username,int version){
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ");
		query.append(PGDAOConstants.harvestScript);
		query.append("(");
		query.append(PGDAOConstants.SCRIPT_OWNER_USERNAME);
		query.append(",");
		query.append(PGDAOConstants.PATH_URL);
		query.append(",");
		query.append(PGDAOConstants.SCRIPT_VERSION);
		query.append(") values(");
		query.append("'");
		query.append(username);
		query.append("'");
		query.append(",");
		query.append("'");
		query.append(path);
		query.append("'");
		query.append(",");
		query.append("'");
		query.append(version);
		query.append("'");
		query.append(");");
		log.info(query.toString());
		System.out.println(query.toString());
		return query.toString();
	}
	private String searchByPath(String path){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		builder.append(PGDAOConstants.SCRIPTID);
		builder.append(" FROM ");
		builder.append(PGDAOConstants.harvestScript);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.PATH_URL);
		builder.append(" LIKE ");
		builder.append("'");
		builder.append(path);
		builder.append("'");
		return builder.toString();
	}
	private String searchPathById(String Id){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		builder.append(PGDAOConstants.PATH_URL);
		builder.append(",");
		builder.append(PGDAOConstants.SCRIPT_OWNER_USERNAME);
		builder.append (" FROM ");
		builder.append(PGDAOConstants.harvestScript);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.SCRIPTID);
		builder.append("=");
		builder.append(Id);

		System.out.println(builder.toString());
		return builder.toString();
		
	}


	@Override
	public String authenticateUser(String name, String password) {

		String insert;
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			String searchQuery = selectUserPassword(name, password);
			log.info(searchQuery);
			ResultSet rs = stmt.executeQuery(searchQuery);
			String id = null;
			if(rs.next()){
				id = rs.getObject(PGDAOConstants.USER_ID).toString();
			}else return null;
			stmt.execute(deleteUserWithID(id));
			stmt.execute(insertAuthToken(name, id));
			rs = stmt.executeQuery(authTokenForUser(id));
			if(rs.next()){
				return rs.getString(PGDAOConstants.AUTH_TOKEN);
			}else return null;
		} catch (Exception e) {
			log.error("Cannot insert harvest Script",e);
			return null;
		}
	
	}
	
	private String selectUserPassword(String name,String password){
		String hash = new ShortAlphanumericIdentifierGenerator().generate(password);
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		builder.append(PGDAOConstants.USER_ID);
		builder.append (" FROM ");
		builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.USER_NAME);
		builder.append(" like '");
		builder.append(name);
		builder.append("' AND ");
		builder.append(PGDAOConstants.PASSWORD_HASH);
		builder.append(" like '");
		builder.append(hash);
		builder.append("'");
		return builder.toString();
	}
	
	private String insertAuthToken(String name,String id){
		String seed = name+(new Date().getTime());
		String hash = new ShortAlphanumericIdentifierGenerator().generate(seed);
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
		builder.append("(");
		builder.append(PGDAOConstants.USER_ID);
		builder.append(",");
		builder.append(PGDAOConstants.USER_AUTH_TOKEN);
		builder.append(") values(");
		builder.append(id);
		builder.append(",");
		builder.append("'");
		builder.append(hash);
		builder.append("'");
		builder.append(");");
		return builder.toString();
	}
	
	private String deleteUserWithID(String id){
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM ");
		builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.USER_ID);
		builder.append("=");
		builder.append(id);
		return builder.toString();
	}
	
	private String authTokenForUser(String id){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT  ");
		builder.append(PGDAOConstants.AUTH_TOKEN);
		builder.append(" FROM " );
		builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.USER_ID);
		builder.append(" = ");
		builder.append(id);
		return builder.toString();
	}

}
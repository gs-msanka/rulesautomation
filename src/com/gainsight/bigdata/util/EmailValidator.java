package com.gainsight.bigdata.util;

import java.io.IOException;

import org.bson.Document;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.pojo.EmailContent;
import com.gainsight.utils.MongoUtil;
import com.mongodb.client.MongoCollection;

public class EmailValidator {

	MongoUtil dbCon = new MongoUtil();
	MongoCollection inboundEmails;
	ObjectMapper mapper = new ObjectMapper();
	/*
	 * #Global mongo mongo.global.host=54.204.251.136 mongo.global.port= 27017
	 * mongo.global.db=test_gsglobaldb mongo.global.username=mdaqa02
	 * mongo.global.password=fgFByS5D mongo.global.connection.pool=100
	 * mongo.global.ssl.enable=false
	 */
	public EmailValidator() {
		// The host,port and dbName are hardcoded because this DB does not
		// change with the profile/env that we are working on...its always in
		// the test global db
		dbCon.createConnection("54.204.251.136", 27017, "test_gsglobaldb");
		inboundEmails = dbCon.getMongoCollection("automatedInboundEmail");
	}

	public void validateEmailContents(EmailContent email) throws JsonParseException, JsonMappingException, IOException {
		@SuppressWarnings("unused")
		Document matchEmail= dbCon.getFirstRecord("automatedInboundEmail", new Document("from_mail",email.getFrom_mail()));
		System.out.println("test o/p:"+matchEmail.toJson().toString());
		EmailContent tovalidate=mapper.readValue(matchEmail.toJson().toString(),EmailContent.class);
		//matchEmail.get
	}
	
	public static void main(String args[]){
		EmailValidator ev=new EmailValidator();
		EmailContent ec=new EmailContent();
		ec.setFrom_mail("example.sender@mandrillapp.com");
		try {
			ev.validateEmailContents(ec);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

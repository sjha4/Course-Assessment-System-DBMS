package com.dbms.basiccheck;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;

import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class Student {
	
	  public static ConnectionGetter cg = null;
	  public static Connection con = null;
	  public static Scanner sc = null;
	  public static String username = null;

		public static void homePage() {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
		    PreparedStatement stmt = null;
			ResultSet rs = null;
			List<String> options = new ArrayList<String>();
			
			try{
				stmt=con.prepareStatement("SELECT col_name FROM MENU_OPTIONS where role = ? and menu_name = ? order by display_order");
				 stmt.setString(1, "S");
			     stmt.setString(2, "Home");

			 rs = stmt.executeQuery();
			 
			 while(rs.next()){
				 options.add(rs.getString("col_name"));
			 }
			 cg.closeStatement(stmt);
			 cg.closeResult(rs);
			for(int i=1; i <= options.size();i++){
				System.out.println(i+ ": "+ options.get(i-1));
			}
			
			int optionNo = sc.nextInt();
			String optionSelected = options.get(optionNo-1);
			if(optionSelected.equals("Logout") || optionSelected.equals("Log out")){
				Entry.startPage();
			}
			else if(optionSelected.equals("View Courses")){
				//System.out.println("View/Add Courses");
				viewAddCourses();
			}
			else if(optionSelected.equals("View Profile") || optionSelected.equals("View/Edit Profile")){
				//System.out.println("View Profile");
				viewProfile();
			}
		        
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}


		private static void viewProfile() {


			// TODO Auto-generated method stub
			PreparedStatement stmt = null;
			ResultSet rs = null;
			System.out.println("Press 0 to go back");
			String name = "";

			try{
				stmt=con.prepareStatement("SELECT name FROM STUDENT where STUDENT_ID = ?");
				stmt.setString(1, username);
				 
				 rs = stmt.executeQuery();
				 while(rs.next()){
					 name = rs.getString("name"); 
				 }
			    
			     }
			catch(Exception e){
			   e.printStackTrace();	
			}
			cg.closeStatement(stmt);
			cg.closeResult(rs);
			String [] nameBreak = name.split("\\s");
			String fname = nameBreak[0];
			String lname = "";
			if(nameBreak.length > 1){
				lname = nameBreak[1];
			}
			System.out.println("First Name: " +  fname);
			System.out.println("Last Name: " +  lname);
			String lastOption = "";
			try{
				stmt=con.prepareStatement("SELECT col_name FROM MENU_OPTIONS where role = ? and menu_name = ? order by display_order");
				 stmt.setString(1, "S");
			     stmt.setString(2, "Profile");
			     
			    rs = stmt.executeQuery();
			    while(rs.next()){
			    	lastOption = rs.getString("col_name");
			    }
			}
			catch(Exception e){
				e.printStackTrace();
			}
			cg.closeStatement(stmt);
			cg.closeResult(rs);
			System.out.println(lastOption + ": " + username);
			int isZero = 1;
			while(isZero != 0){
				isZero = sc.nextInt();
				}
			homePage();
			
		}
		
        private static void viewAddCourses(){
        	PreparedStatement stmt = null;
			ResultSet rs = null;
			System.out.println("Press 0 to go back");
			System.out.println("The courses enrolled by you are:");
			List<String> courses = new ArrayList<String>();
			try {
				stmt=con.prepareStatement("SELECT course_id FROM course_student WHERE student_id = ? ");
				stmt.setString(1, username);
				rs=stmt.executeQuery();
				//int i=0;
				while (rs.next()) {
					courses.add(rs.getString("COURSE_ID"));
					//System.out.println(++i + ". " +  rs.getString("course_id"));
				}
				for(int i=1; i <= courses.size();i++){
					 System.out.println(i + ": " + courses.get(i-1));
				 }
			}
			catch(Exception e) { e.printStackTrace();
				System.out.println("Encountered an Error: "+e.getMessage());
				viewAddCourses();
			}
			cg.closeStatement(stmt);
			cg.closeResult(rs);
			System.out.println("Enter course id to view the course details:");
			int temp=sc.nextInt();
			String tempCourse = courses.get(temp - 1);
			if (!isNumber(tempCourse)) {
				System.out.println("choose one of the following options");
			    System.out.println("1. Current Open Homeworks");
			    System.out.println("2. Past Homeworks");
			    System.out.println("Press 0 to go back");
			    int temp1 = sc.nextInt();
			    java.sql.Date stDateSqlFormat = null;
			    if (temp1==1) {
			    	System.out.println("All open homeworks are:");
			    	try {
			    		stmt=con.prepareStatement("SELECT e.exercise_id FROM exercise e, course_student cs WHERE e.course_id=cs.course_id AND e.deadline > ? AND cs.student_id = ? ");
			    		//DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			    		//java.sql.Date stDateSqlFormat = null;
			    		Date startDate = new Date();
			    		stDateSqlFormat = new java.sql.Date(startDate.getTime());
			    		stmt.setDate(1, stDateSqlFormat);
			    		
			    		stmt.setString(2, username);
			    		rs=stmt.executeQuery();
			    		int i=0;
			    		while (rs.next()) {
			    			System.out.println(++i + ". " + rs.getString("exercise_id"));
			    		}
			    	}
			    	catch(Exception e) { e.printStackTrace();
			    		System.out.println("Encountered an Error: "+e.getMessage());
						viewAddCourses(); 
					}
			    	cg.closeStatement(stmt);
					cg.closeResult(rs);
					System.out.println("Enter Exercise ID to attempt or enter 0 to go back");
					int ex_id = sc.nextInt();
					if (ex_id!=0) {
						try {
							stmt=con.prepareStatement("Select sc_mode,retries FROM exercise WHERE exercise_id = ?");
							stmt.setInt(1, ex_id);
							rs=stmt.executeQuery();
							while(rs.next()) {
								if(rs.getString("sc_mode").equalsIgnoreCase("standard")) {
									
									int retriesAllowed = rs.getInt("retries");
									takeStandardTest(ex_id,retriesAllowed);
									viewAddCourses();
								}
								else if(rs.getString("sc_mode").equalsIgnoreCase("adaptive")) {
									int retriesAllowed = rs.getInt("retries");
									takeAdaptiveTest(ex_id,retriesAllowed,tempCourse);
									viewAddCourses();
								}
							}
						}
						catch(Exception e){
							System.out.println("Encountered an Error: "+e.getMessage());
							viewAddCourses();
						}
						cg.closeStatement(stmt);
						cg.closeResult(rs);
					}
					else {
						if (ex_id==0)
							viewAddCourses();
						else {
							int isZero = 1;
							while(isZero != 0){
								System.out.println("Enter 0 to go back!!!");
								isZero = sc.nextInt();
								}
							homePage();
						}
					}
						
					
			    }
			    else if (temp1==2) {
			    	java.sql.Date deadlineSqlDate = null;
			    	try {
			    		stmt=con.prepareStatement("SELECT e.start_date, e.end_date, e.sc_mode,(e.total_questions * e.points) AS total_points,e.scoring_policy,e.retries - (Select count(Attempt_Id) from Attempt_Submission where Exercise_Id = e.Exercise_id AND student_id = ?) AS available_attempts,(Select count(Attempt_Id) from Attempt_Submission where Exercise_Id = e.Exercise_id AND student_id = ?) as attempts_by_student FROM exercise e, attempt_submission a_s WHERE a_s.exercise_id=e.exercise_id AND a_s.student_id = ? and e.DEADLINE < ?");
			    		stmt.setString(1, username);
			    		stmt.setString(2, username);
			    		stmt.setString(3, username);
			    		Date startDate = new Date();
			    		deadlineSqlDate = new java.sql.Date(startDate.getTime());
			    		stmt.setDate(4, deadlineSqlDate);
			    		rs=stmt.executeQuery();
			    		System.out.println("Past Homeworks for the course:");
			    		while (rs.next()) {
			    			System.out.println(rs.getString("start_date")+"  "+rs.getString("end_date")+"  "+rs.getString("sc_mode")+"  "+rs.getString("total_points")+"  "+rs.getString("scoring_policy")+"  "+rs.getString("avaiable_attempts")+"  "+rs.getString("attempts_by_student"));
			    		}
			    		cg.closeStatement(stmt);
						cg.closeResult(rs);
			    	}
			    	catch(Exception e) { e.printStackTrace();
			    		System.out.println("Encountered an Error: "+e.getMessage());
						viewAddCourses();
					}	
			    	
					System.out.println("Enter 0 to go back");
					int isZero = 1;
					while(isZero != 0){
						isZero = sc.nextInt();
						}
					viewAddCourses();
			    }
			    else{
			    	if (temp1==0)
			    	viewAddCourses();
			    	else {
						int isZero = 1;
						while(isZero != 0){
							System.out.println("Enter 0 to go back!!!");
							isZero = sc.nextInt();
							}
						viewAddCourses();
					}
			    }
			}

			else { 
				if (Integer.parseInt(tempCourse)==0)
		    	homePage();
				else {
					int isZero = 1;
					while(isZero != 0){
						System.out.println("Enter 0 to go back!!!");
						isZero = sc.nextInt();
						}
					homePage();
				}
					
			}	
        	
        }
        
        private static boolean isNumber(String x){
    		try
    	    {
    	        Integer.parseInt(x);
    	    }
    	    catch(NumberFormatException ex)
    	    {
    	        return false;
    	    }
    	    return true;
    	}
        
        public static void takeStandardTest(int ex_id, int retriesAllowed) {
        	List<String> questionsList = new ArrayList<String>();
        	List<SubmissionResult> srAttributes= new ArrayList<SubmissionResult>();
        	List<String> correctness=new ArrayList<String>();
        	PreparedStatement stmt= null;
    		ResultSet rs=null;
    		int attempt_id=0;
    		int attempt_number=0;
    		int retries = retriesAllowed;
    		int total_points=0;
    		int correct_points=0;
    		int wrong_points=0;
    		//boolean exercise_exists=false;
    		try {
	    		stmt=con.prepareStatement("SELECT count(*) AS number_of_attempts FROM attempt_submission WHERE exercise_id = ? And student_Id =?");
	    		stmt.setInt(1, ex_id);
	    		stmt.setString(2, username);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			attempt_number = rs.getInt("number_of_attempts"); 
	    			System.out.println("Attempts done: " + attempt_number);
	    		}
	    	}
	    	catch(Exception e) { e.printStackTrace();
	    		System.out.println("Encountered an Error: "+e.getMessage());
	    		takeStandardTest(ex_id,retriesAllowed);
			}	
        	cg.closeStatement(stmt);
			cg.closeResult(rs);
			
			if (attempt_number >= retries && retries != -1) {
				System.out.println("No more attempts left!!!");
				return;
			}
				
			
        	try {
	    		stmt=con.prepareStatement("SELECT question_id FROM exercise_question WHERE exercise_id = ?");
	    		stmt.setInt(1, ex_id);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			questionsList.add(rs.getString("question_id"));
	    			System.out.println("Question Ids:" + rs.getString("question_id"));
	    		}
	    	}
	    	catch(Exception e) { e.printStackTrace();
	    		System.out.println("SELECT question_id FROM exercise_question WHERE exercise_id = ?");
	    		System.out.println("Encountered an Error: "+e.getMessage());
	    		takeStandardTest(ex_id,retriesAllowed);
			}	
        	cg.closeStatement(stmt);
			cg.closeResult(rs);
			Collections.shuffle(questionsList);
			for (int i=0; i<questionsList.size(); i++) {
				List<String> questionsTextList=new ArrayList<String>();
				List<String> answersCorrectList=new ArrayList<String>();
				List<String> answersWrongList=new ArrayList<String>();
				HashMap<Integer,String> h_options=new HashMap<>();
				String answerAttempt=null;
				String answer_result= null;
				try {
		    		stmt=con.prepareStatement("SELECT question FROM question_param_answers WHERE question_id = ?");
		    		stmt.setInt(1, Integer.parseInt(questionsList.get(i)));
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			questionsTextList.add(rs.getString("question"));
		    		}
		    	}
		    	catch(Exception e) { e.printStackTrace();
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		takeStandardTest(ex_id,retriesAllowed);
				}	
	        	cg.closeStatement(stmt);
				cg.closeResult(rs);				
				Collections.shuffle(questionsTextList);
				String questionText=questionsTextList.get(0);
				int index=0;
				System.out.println(++index + ". " + questionText);
				try {
		    		stmt=con.prepareStatement("SELECT answer FROM question_param_answers WHERE question = ? AND correct=1");
		    		stmt.setString(1, questionText);
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			answersCorrectList.add(rs.getString("answer"));
		    		}
		    	}
		    	catch(Exception e) { e.printStackTrace();
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		takeStandardTest(ex_id,retriesAllowed);
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
				try {
		    		stmt=con.prepareStatement("SELECT answer FROM question_param_answers WHERE question = ? AND correct=0");
		    		stmt.setString(1, questionText);
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			answersWrongList.add(rs.getString("answer"));
		    		}
		    	}
		    	catch(Exception e) { e.printStackTrace();
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		takeStandardTest(ex_id,retriesAllowed);
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
				Collections.shuffle(answersCorrectList);
				Collections.shuffle(answersWrongList);
				List<String> answerOptions=new ArrayList<String>();
				answerOptions.add(answersCorrectList.get(0));
				answerOptions.add(answersWrongList.get(0));
				answerOptions.add(answersWrongList.get(1));
				answerOptions.add(answersWrongList.get(2));
				
				Collections.shuffle(answerOptions);
				
				for (int j=0;j<answerOptions.size();j++) {
					System.out.println(j+1 + ". " + answerOptions.get(j));
					h_options.put(j+1, answerOptions.get(j));
				}
				int answerOption=sc.nextInt();
				answerAttempt = h_options.get(answerOption);
				try {
		    		stmt=con.prepareStatement("SELECT correct FROM question_param_answers WHERE question = ? AND answer = ?");
		    		stmt.setString(1, questionText);
		    		stmt.setString(2, answerAttempt);
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			answer_result=rs.getString("correct");
		    			correctness.add(answer_result);
		    		}
		    	}
		    	catch(Exception e) { e.printStackTrace();
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		takeStandardTest(ex_id,retriesAllowed);
				}
				SubmissionResult values=new SubmissionResult(Integer.parseInt(questionsList.get(i)),questionText,answerAttempt,Integer.parseInt(answer_result));
				srAttributes.add(values);
				
				cg.closeStatement(stmt);
				cg.closeResult(rs);
			}
			
			attempt_number++;
			
			try {
	    		stmt=con.prepareStatement("SELECT points,penalty FROM exercise WHERE exercise_id=?");			    		
	    		stmt.setInt(1, ex_id);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			correct_points=Integer.parseInt(rs.getString("points"));
	    			wrong_points=Integer.parseInt(rs.getString("penalty"));
	    		}
	    			
	    	}
	    	catch(Exception e) { e.printStackTrace();
	    		System.out.println("Encountered an Error: "+e.getMessage());
	    		takeStandardTest(ex_id,retriesAllowed);
			}
			
			
			
			for (int i=0;i<correctness.size();i++) {
				if(correctness.get(i).equals("0")) total_points-=wrong_points;
				else if(correctness.get(i).equals("1")) total_points+=correct_points;
			}
			
//			try {
//				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//	    		stmt=con.prepareStatement("INSERT INTO attempt_submission (exercise_id,student_id,submission_time,points,number_of_attempts) VALUES (?,?,TO_DATE(?, 'MM/DD/YYYY'),?,?)");			    		
//	    		stmt.setInt(1, ex_id);
//	    		stmt.setString(2, username);
//	    		stmt.setString(3, df.format(new Date()));
//	    		stmt.setInt(4,total_points);
//	    		stmt.setInt(5, attempt_number);
//	    		rs=stmt.executeQuery();
//			}
//
//	    	catch(Exception e) { e.printStackTrace();
//	    		System.out.println("Encountered an Error: "+e.getMessage());
//	    		takeStandardTest(ex_id,retriesAllowed);
//			}
			cg.closeStatement(stmt);
			cg.closeResult(rs);
			CallableStatement callableStatement = null;
			try {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				String retrieveAttemptId = "{call INSERT_AS_AND_RETURN_ID(?,?,?,?,?,?)}";
				callableStatement = con.prepareCall(retrieveAttemptId);
				callableStatement.setInt(1,ex_id);
				callableStatement.setString(2, username);
				callableStatement.setString(3, df.format(new Date()));
				callableStatement.setInt(4, total_points);
				callableStatement.setInt(5, attempt_number);
				callableStatement.registerOutParameter(6, OracleTypes.CURSOR);
				callableStatement.executeUpdate();
				rs = (ResultSet) callableStatement.getObject(6);
				
				while(rs.next()){
					attempt_id = rs.getInt("ret_id");
				}
				cg.closeResult(rs);
				cg.closeStatement(callableStatement);
			}

	    	catch(Exception e) { e.printStackTrace();
	    		System.out.println("Encountered an Error: "+e.getMessage());
	    		takeStandardTest(ex_id,retriesAllowed);
			}
//			cg.closeStatement(stmt);
//			cg.closeResult(rs);
//			
			for (int i=0; i<srAttributes.size();i++) {
				SubmissionResult attribute=srAttributes.get(i);
				stmt = null;
				rs = null;
				try {
		    		stmt=con.prepareStatement("INSERT INTO submission_result (attempt_id,question_id,question,answer,correct) VALUES (?,?,?,?,?)");			    		
		    		System.out.println(""+attempt_id);
		    		stmt.setInt(1, attempt_id );
		    		stmt.setInt(2, attribute.questionId);
		    		stmt.setString(3, attribute.question);
		    		stmt.setString(4,attribute.answer);
		    		stmt.setInt(5, attribute.correct);
		    		rs=stmt.executeQuery();
				}

		    	catch(Exception e) { e.printStackTrace();
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		takeStandardTest(ex_id,retriesAllowed);
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
			}
			
			
	     	
        }
        
        public static void takeAdaptiveTest(int ex_id, int retriesAllowed,String course_id) {  
        	try{
        	List<SubmissionResult> srAttributes= new ArrayList<SubmissionResult>();
        	List<Integer> questionList = new ArrayList<>();
        	List<String> correctness=new ArrayList<>();
			HashMap<Integer,List<String>> DQ=new HashMap<>();
        	String topic_id=null;
        	int attempt_id=0;
    		int attempt_number=0;
    		int total_points=0;
    		int correct_points=0;
    		int wrong_points=0;
    		int total_questions=0;
    		PreparedStatement stmt= null;
    		ResultSet rs=null;
    		boolean topic_exists=false;
    		try {
	    		stmt=con.prepareStatement("SELECT total_questions FROM exercise WHERE exercise_id=?");
	    		stmt.setInt(1, ex_id);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			total_questions=rs.getInt("total_questions");
	    		}
	    	}
	    	catch(Exception e) {
	    		System.out.println("Encountered an Error in retrieving total questions: "+e.getMessage());
	    		viewAddCourses();
			}
			cg.closeStatement(stmt);
			cg.closeResult(rs);
    		try {
	    		stmt=con.prepareStatement("SELECT count(*) AS number_of_attempts FROM attempt_submission WHERE exercise_id = ? AND STUDENT_ID=?");
	    		stmt.setInt(1, ex_id);
	    		stmt.setString(2,username);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			attempt_number = rs.getInt("number_of_attempts");
	    			System.out.println("Attempts done: " + attempt_number);
	    		}
	    	}
	    	catch(Exception e) {
	    		System.out.println("");
	    		System.out.println("Encountered an Error in getting attempts count: "+e.getMessage());
	    		viewAddCourses();
			}	
        	cg.closeStatement(stmt);
			cg.closeResult(rs);
			if (attempt_number >= retriesAllowed && retriesAllowed != -1) {
				System.out.println("No more attempts left!!!");
				return;
			}
			try {
	    		stmt=con.prepareStatement("SELECT topic_id, count(topic_id) AS topic_exists FROM adaptive_exercise_topic WHERE exercise_id = ?");
	    		stmt.setInt(1, ex_id);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			int temp = rs.getInt("topic_exists");
	    			topic_id = rs.getString("topic_id");
	    			if (temp==1) topic_exists=true;
	    		}
	    	}
	    	catch(Exception e) {
	    		System.out.println("Encountered an Error in topic wise adaptive exercise: "+e.getMessage());
	    		//takeAdaptiveTest(ex_id,retriesAllowed);
			}
			cg.closeStatement(stmt);
			cg.closeResult(rs);
			if(topic_exists) {
				try {
		    		stmt=con.prepareStatement("SELECT q.question_id FROM question q, adaptive_exercise_topic aet  WHERE q.topic_id=aet.topic_id and aet.topic_id = ?");
		    		stmt.setString(1, topic_id);
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			questionList.add(rs.getInt("question_id"));
		    		}
		    	}
		    	catch(Exception e) {
		    		System.out.println("If topic exists");
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		//takeAdaptiveTest(ex_id,retriesAllowed);
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);				
			}
			else {
				try {
		    		stmt=con.prepareStatement("SELECT q.question_id FROM question q, question_bank qb WHERE q.question_id=qb.question_id and qb.course_id = ?");
		    		stmt.setString(1, course_id);
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			questionList.add(rs.getInt("question_id"));
		    		}
		    	}
		    	catch(Exception e) {
		    		System.out.println("Encountered an Error: "+e.getMessage());
		    		System.out.println("Encountered an Error in Question Bank query: "+e.getMessage());
		    		//takeAdaptiveTest(ex_id,retriesAllowed);
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);	
			}
			for (int i=0; i<questionList.size(); i++) {
				try {
		    		stmt=con.prepareStatement("SELECT question_text,difficulty_level FROM question WHERE question_id = ?");
		    		stmt.setInt(1, questionList.get(i));
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			if (DQ.containsKey(rs.getInt("difficulty_level"))) {
		    				List<String> l = DQ.get(rs.getInt("difficulty_level"));
		    				Collections.shuffle(l);
		    				l.add(rs.getString("question_text"));
		    				DQ.put(rs.getInt("difficulty_level"),l);
		    			}
		    			else {
		    				List<String> l = new ArrayList<String>();
		    				l.add(rs.getString("question_text"));
		    				DQ.put(rs.getInt("difficulty_level"),l );
		    			}
		    		}
		    	}
		    	catch(Exception e) {
		    		System.out.println("Encountered an Error in querying question and difficulty level: "+e.getMessage());
		    		viewAddCourses();
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
			}
			int index=0;
			int temp_difficulty=3;
			
			for (int j=0; j<total_questions;j++) {
				int q_id=0;
				String answerAttempt=null;
				List<String> temp=new ArrayList<>();
				List<String> answersCorrectList=new ArrayList<>();
				List<String> answersWrongList=new ArrayList<>();
				temp= DQ.get(temp_difficulty);
				String answer_result=null;
				//Shuffling needed
				Collections.shuffle(temp);
				System.out.println(++index + ". " + temp.get(0));
				try {
			   		stmt=con.prepareStatement("SELECT answer FROM question_param_answers WHERE question = ? AND correct=1");
			   		stmt.setString(1, temp.get(0));
			   		
			   		rs=stmt.executeQuery();
			    	while (rs.next()) {
			    		answersCorrectList.add(rs.getString("answer"));
			   		}
			   	}
			   	catch(Exception e) {
					System.out.println("SELECT answer FROM question_param_answers WHERE question = ? AND correct=1");
			   		e.printStackTrace();
					
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
				try {
			    	stmt=con.prepareStatement("SELECT answer FROM question_param_answers WHERE question = ? AND correct=0");
			   		stmt.setString(1, temp.get(0));
			   		rs=stmt.executeQuery();
			   		while (rs.next()) {
			   			answersWrongList.add(rs.getString("answer"));
		    		}
		    	}
			    catch(Exception e) {
			    	System.out.println("SELECT answer FROM question_param_answers WHERE question = ? AND correct=0");
			   		
					e.printStackTrace();
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
					
				Collections.shuffle(answersCorrectList);
				Collections.shuffle(answersWrongList);
				List<String> answerOptions=new ArrayList<String>();
				HashMap<Integer,String> h_options=new HashMap<>();
				answerOptions.add(answersCorrectList.get(0));
     			answerOptions.add(answersWrongList.get(0));
				answerOptions.add(answersWrongList.get(1));
				answerOptions.add(answersWrongList.get(2));
					
				Collections.shuffle(answerOptions);
					
				for (int k=0;k<answerOptions.size();k++) {
					System.out.println(k+1 + ". " + answerOptions.get(k));
					h_options.put(k+1, answerOptions.get(k));
				}
					
				int answerOption=sc.nextInt();
				answerAttempt = h_options.get(answerOption);
				try {
		    		stmt=con.prepareStatement("SELECT correct FROM question_param_answers WHERE question = ? AND answer = ?");
		    		stmt.setString(1, temp.get(0));
		    		stmt.setString(2, answerAttempt);
		    		//System.out.println("Selecting correct/Incorrect query");
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			answer_result=rs.getString("correct");
		    			if (answer_result.equals("0") && temp_difficulty!=1)
		    				temp_difficulty--;
		    			else if (answer_result.equals("1") && temp_difficulty!=6)
		    				temp_difficulty++;
		    			correctness.add(answer_result);
		    		}
		    	}
		    	catch(Exception e) {
					e.printStackTrace();
					System.out.println("Error in getting correct from QPa");
				}
				/*
				 * Assuming using queu question text
				 */
				try {
		    		stmt=con.prepareStatement("SELECT question_id FROM question_param_answers WHERE question = ?");
		    		stmt.setString(1, temp.get(0));
		    		System.out.println("Selecting Question ID");
		    		rs=stmt.executeQuery();
		    		while (rs.next()) {
		    			q_id=rs.getInt("question_id");
		    		}
		    	}
		    	catch(Exception e) {
					e.printStackTrace();
					System.out.println("SELECT question_id FROM question_param_answers WHERE question = ?");
			   		
				}
				/*
				 * this part was to set q_id of asked question
				 */
				SubmissionResult values = new SubmissionResult(q_id,temp.get(0),answerAttempt,Integer.parseInt(answer_result));
				srAttributes.add(values);
				
				cg.closeStatement(stmt);
				cg.closeResult(rs);
			}
			System.out.println("Creating Submission Result");
			attempt_number++;
			try {
	    		stmt=con.prepareStatement("SELECT points,penalty FROM exercise WHERE exercise_id=?");			    		
	    		stmt.setInt(1, ex_id);
	    		rs=stmt.executeQuery();
	    		while (rs.next()) {
	    			correct_points=Integer.parseInt(rs.getString("points"));
	    			wrong_points=Integer.parseInt(rs.getString("penalty"));
	    		}
	    			
	    	}
	    	catch(Exception e) {
				e.printStackTrace();
				System.out.println("Encountered an Error in getting penalty and points");
			}

			for (int i=0;i<correctness.size();i++) {
				if(correctness.get(i).equals("0")) total_points-=wrong_points;
				else if(correctness.get(i).equals("1")) total_points+=correct_points;
			}	
			
			CallableStatement callableStatement = null;
			try {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				String retrieveAttemptId = "{call INSERT_AS_AND_RETURN_ID(?,?,?,?,?,?)}";
				callableStatement = con.prepareCall(retrieveAttemptId);
				callableStatement.setInt(1,ex_id);
				callableStatement.setString(2, username);
				callableStatement.setString(3, df.format(new Date()));
				callableStatement.setInt(4, total_points);
				callableStatement.setInt(5, attempt_number);
				callableStatement.registerOutParameter(6, OracleTypes.CURSOR);
				callableStatement.executeUpdate();
				rs = (ResultSet) callableStatement.getObject(6);
				
				while(rs.next()){
					attempt_id = rs.getInt("ret_id");
				}
				cg.closeResult(rs);
				cg.closeStatement(callableStatement);
			}

	    	catch(Exception e) {
				e.printStackTrace();
			}
				
			for (int i=0; i<srAttributes.size();i++) {
				SubmissionResult attribute=srAttributes.get(i);
				try {
					stmt=con.prepareStatement("INSERT INTO submission_result (attempt_id,question_id,question,answer,correct) VALUES (?,?,?,?,?)");			    		
		    		System.out.println(""+attempt_id);
		    		stmt.setInt(1, attempt_id );
		    		stmt.setInt(2, attribute.questionId);
		    		stmt.setString(3, attribute.question);
		    		stmt.setString(4,attribute.answer);
		    		stmt.setInt(5, attribute.correct);
		    		rs=stmt.executeQuery();
				}

		    	catch(Exception e) {
					e.printStackTrace();
				}
				cg.closeStatement(stmt);
				cg.closeResult(rs);
			}	
						
			
        	}
        	catch(Exception e){
        		System.out.println("General exception");
        		e.printStackTrace();
        	}
        }

}

class SubmissionResult{
	int questionId;
	String question;
	String answer;
	int correct;
	public SubmissionResult(int questionId, String question, String answer, int correct) {
		this.questionId = questionId;
		this.question = question;
		this.answer = answer;
		this.correct = correct;
	}
}
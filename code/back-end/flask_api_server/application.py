from flask import Flask, render_template, redirect, url_for, request
import mysql.connector
from time import time, ctime, sleep

#Initalised the flask server.
application = Flask(__name__)

#This establishes a connection with the database using the entered credentials.
db = mysql.connector.connect(
	host="database-users.cwhtfwf8upc3.eu-west-1.rds.amazonaws.com", #Where our database is hosted - Amazon Web Service Elasticbeanstalk is used here.
	user="admin", #Username for the remote database.
	passwd="BortasCogan2020!", #The password to access the remote database.
	database="user_details" #The table being used.
	)


#Creates a connection with the database.
cur = db.cursor()

#End point that takes you to the login page.
@application.route("/login", methods=["POST", "GET"]) #This allows POST and GET requests.
def login():

	#This is a variable set to none just incase the data return no value.
	fetchdata = None

	# If the method we receive is a POST method then we redirect the inputed name in the form and build a table with the inputed information.
	if request.method == "POST":
		info = request.form # This is where the information is receive from POST requests.
		username = info["Username"] #This is where the user's username is received.
		password = info["Password"] #This is the user's password is received.

		#Creates a connection with the database
		cur = db.cursor()

		#Make a request to the database
		sql_query = "SELECT * FROM user_info where username = " + "'" + username + "' and password = " + "'" + password + "'"
		cur.execute(sql_query) # executes the SQL request to the database.
		for row in cur.fetchall(): # This retrieves the return information from the database.
			fetchdata = row
		cur.close() #This closes the connection with the database.

		#Verifies if the username and password are valid. If the is nothing returned theis will cause a 400 #Failure status code.error code otherwose the server returns a 200 #successful status code. #Success status codestatus message
		if fetchdata == None:
			return "Failure to Login", 400 #Failure status code. #Failure status code.#Failure status code.
		else:
			return "Correct Username and Password", 200 #successful status code. #Success status code#Success status code.

		#Returns the information to the device that made the request.
		return fetchdata

#This registers the user onto the database.
@application.route("/register", methods=["POST", "GET"]) #This allows POST and GET requests.
def register():


	fetchdata = None
	# If the method received is a POST request then we have to check if the username is not taken or a blank and then of successfully push to the information to the database.
	if request.method == "POST":
		info = request.form #This is where the POST requests information is received.
		username = info["Username"] # We just take the username from the POST request received.
		password = info["Password"] # We just take the password from the POST request received.

		#Creates a connection with the database
		con = db
		cur = db.cursor()

		#Make a request to the database
		sql_query = "SELECT * FROM user_info where username = " + "'" + username + "'"
		cur.execute(sql_query) # executes the SQL request to the database.
		for row in cur.fetchall(): # This retrieves the return information from the database.
			fetchdata = row

		#If there is username is already in the databse then return an error status code.
		if fetchdata != None:
			fetchdata = "Another user used the username. Please chose another username.", 400 #Failure status code.
			return fetchdata

		#If the username or password is blank then return an error status code.
		elif username == '' or password == '':
			fetchdata = "Cannot have a blank username or password", 400 #Failure status code.
			return fetchdata

		#Insert the username and password into the database
		insert_query = "INSERT INTO user_info (username, password) VALUES (%s, %s)" #SQL statement that passes the information to the user_info database table
		insert_values = (username, password)
		cur.execute(insert_query, insert_values) # This executes the SQL statement with the username and password variables being placed into the SQL statement.


		insert_query = "INSERT INTO meal (username) VALUES (" + "'" + username + "'"+ ")" #SQL statement that passes the username to the meal database table
		cur.execute(insert_query) #This executes the SQL statement.

		insert_query = "INSERT INTO food_micro (username) VALUES (" + "'" + username + "'"+ ")" #SQL statement that passes the username to the food_micro database table
		cur.execute(insert_query) #This executes the SQL statement.

		try:
			con.commit() # This commits all executed statements to the database
			cur.close() #This closes the connection with the database.
			return "Your are successfully registered", 200 #successful status code. #Success status code

		except Exception as e:
			return "An error has occured, please try again", 400 #Failure status code. #Failure status code.




@application.route("/delete_account", methods=["POST", "GET"]) #This allows for POST and GET requests to be sent and received respectively.
def delete_account():

	fetchdata = None
	# If the method we receive is a POST.
	if request.method == "POST":
		info = request.form # This is where the information is received from the sent POST request.
		username = info["Username"]

		#If username is blank then an error status code is returned.
		if username == '':
			return "No username received", 400 #Failure status code. #Failure status code.#This is an error staus code.

		#Creates a connection with the database
		con = db
		cur = db.cursor()

		#This queries the database to check if the username is in the database.
		sql_query = "SELECT * FROM user_info where username = " + "'" + username + "'"
		cur.execute(sql_query) # executes the SQL request to the database.
		for row in cur.fetchall(): # This retrieves the return information from the database.
			fetchdata = row

		if fetchdata == None:
			fetchdata = "Username not in database", 400 #Failure status code.


		#Delete from the user_info database
		delete_query = "DELETE FROM user_info WHERE username = " + "'" + username + "'"
		cur.execute(delete_query) # This executes the SQL statement.

		#Delete from the meal database
		delete_query = "DELETE FROM meal WHERE username = " + "'" + username + "'"
		cur.execute(delete_query) # This executes the SQL statement.

		#Delete from the food_micro database
		delete_query = "DELETE FROM food_micro WHERE username = " + "'" + username + "'"
		cur.execute(delete_query) # This executes the SQL statement.

		try:
			con.commit() # This commits all executed statements to the database
			cur.close() #This closes the connection with the database.
			return "Your account is successfully deleted", 200 #successful status code.

		except Exception as e:
			return "An error has occured, please try again", 400 #Failure status code.


@application.route("/update_micro", methods=["POST", "GET"]) #This allows for POST and GET requests to be sent and received respectively.
def update_micro():

	# If the method we receive is a POST method then we redirect the inputed name in the form and build a table with the inputed information.
	fetchdata = None
	if request.method == "POST":
		info = request.form # This is where the information is received from the sent POST request.
		username = info["Username"]
		value = info["Value"] # number - amount
		type = info["Type"] # calories, fat, carbs, protein, activity, breakfast, lunch, dinner
		action = info["Action"]# delete or add

		#if the type is not equal to None then make sure all the letters are lowercase.
		if type != None:
			type = type.lower() # This converts the string to lowercase characters. #

		#If the inputted values are black the returne an error status code.
		if username == '' or value == '' or type == '' or action == '':
			return "username, value, type, and action cannot be blank", 400 #Failure status code.

		#If the values is entered is negative then return an error status code.
		elif float(value) < 0:
			return "value cannot have a negative integer", 400 #Failure status code.


		value = float(value) #Converts the value input to a float.

		#Creates a connection with the database
		con = db
		cur = db.cursor()

		#Make a request to the database to retrieve any info that is on the database for the inputted type.
		sql_query = "SELECT " + type + " FROM food_micro where username = %s"
		cur.execute(sql_query, (username,)) # This executes the SQL statement.
		for row in cur.fetchall(): # This retrieves the return information from the database.
			micro = row[0]

		#If the action is add the we execute the code below.
		if action == "add":
			#If the database returns nothing then just add the entered value to the databse.
			if micro == None:
				micro = float(value)

			#If the database returns a value then add the inputted value to the value retrieved from the database.
			else:
				micro = value + float(micro)

		#If type is equal to delete.
		elif action == "delete":
			#If value is negative return set micro to none.
			if value > float(micro):
				micro == None

			#Otherwise take the inputed value away from the retrieved value from the database.
			else:
				micro = float(micro) - value

		#If the type is equal to overwrite then overwrite the value in the database.
		elif action == "overwrite":
			micro = float(value)

		#Update the database

		insert_query = "UPDATE food_micro SET " + type + " = %s WHERE username = %s"
		try:
			cur.execute(insert_query, (micro, username,)) # Executes the SQL statement.
			con.commit() # This commits all executed statements to the database
			cur.close() #This closes the connection with the database.
			return "Update successfully completed", 200 #successful status code.

		except Exception as e:
			return "An error has occured, please try again", 400 #Failure status code.


@application.route("/update_food", methods=["POST", "GET"]) #This allows for POST and GET requests to be sent and received respectively.
def update_food():

	#This sets fetch to None incase the database returns nothing.
	fetchdata = None

	# If the method we receive is a POST request.
	if request.method == "POST":
		info = request.form # This is where the information is received from the sent POST request.
		username = info["Username"] #This is the recieved username.
		type = str(info["Type"]) # This can be breakfast, lunch, dinner, or snack.
		action = info["Action"] # This can be add. delete, or ovewrite.
		food = info["Food"] # This contains the food the user has eaten along with other values in a serialised style.

		#if the type is not equal to None then make sure all the letters are lowercase.
		if type != None:
			type = type.lower() # This converts the string to lowercase characters.

		if username == '' or type == '' or action == '':
			return "username, value, type, and action cannot be blank", 400 #Failure status code.

		#Creates a connection with the database
		con = db
		cur = db.cursor()

		#Make a request to the database
		sql_query = "SELECT " + type + " FROM meal where username = %s"
		cur.execute(sql_query, (username,)) #This executes an SQL statement.
		for row in cur.fetchall(): # This retrieves the return information from the database.
			micro = row[0]

		#If the action is add the we execute the code below.
		if action == "add":
			#If None then the food is added to the table.
			if micro == None:
				micro = food
				insert_query = "UPDATE meal SET " + type + " = %s WHERE username = %s"
				cur.execute(insert_query, (micro, username,)) #excutes teh SQL statement.

			#otherwise, if the there is a value in the database then add the food to the existing food on the database.
			else:
				micro = micro + "|" + food #Foods are divided with a bar.
				insert_query = "UPDATE meal SET " + type + " = %s WHERE username = %s"
				cur.execute(insert_query, (micro, username,)) #Executes a SQL statement.

		#If the action equals delete then execute this code.
		elif action == "delete":
			#If the database contains no values then add the inputted value to the database.
			if micro == None:
				micro == None
				insert_query = "UPDATE meal SET " + type + " = %s WHERE username = %s"
				cur.execute(insert_query, (micro, username,)) #Executes the SQL statement.

			#Otherwise it deserialised the the food string and removes the desired fod from the list.
			else:
				deserialised_food = micro.split("|") # Splits the foods into a list.

				#If the database only has more than one food stored on it then deserialise the insormation then remove the desired value.
				if food in deserialised_food and len(deserialised_food) > 1:
					deserialised_food.remove(food) # remove the desired food from the database.

					micro = "|".join(deserialised_food) #Serialise the remaining foods on the database.

					insert_query = "UPDATE meal SET " + type + " = %s WHERE username = %s"
					cur.execute(insert_query, (micro, username,)) # execute the SQL statement.

				#If the database contains one or less foods on the table then we have to remove the desired food and set the database to NULL for the cell in th database.
				elif food in deserialised_food and len(deserialised_food) <= 1:
					deserialised_food.remove(food) # Removes the desired food from the database.

					insert_query = "UPDATE meal SET " + type + " = NULL WHERE username = %s"
					cur.execute(insert_query, (username,)) #Executes the SQL statement.

		#If action equals overwrite then the value on the table will be overwritten by the inputed value.
		elif action == "overwrite":
			#If the food input is blank then set the database cell to NULL.
			if food == "":
				insert_query = "UPDATE meal SET " + type + " = NULL WHERE username = %s"
				cur.execute(insert_query, (username,)) #Execute the SQL statement.

			#Otherwise just overwrite the value in the cell of the database.
			else:
				micro = food
				insert_query = "UPDATE meal SET " + type + " = %s WHERE username = %s"
				cur.execute(insert_query, (micro, username,)) #Executes the SQL statement.

		try:
			con.commit() # This commits all executed statements to the database
			cur.close() #This closes the connection with the database.
			return "Update successfully completed", 200 #successful status code.

		except Exception as e:
			return "An error has occured, please try again", 400 #Failure status code.


@application.route('/daily_reset', methods=["POST", "GET"]) #This allows for POST and GET requests to be sent and received respectively.
def daily_reset():
	cur = db.cursor() #Establishes a connection with the database.

	#If the information is a POST request then execute theis code.
	if request.method == "POST":
		info = request.form # This is where the information is received from the sent POST request.
		key = info["Key"] #Specialaadmin password that allows the admin to overide the daily_reset at midnight and reset all the values on the database on demand.

		#If the key is equal to the password.
		if key == 'BortasCogan2020!':
			sql_query = "SELECT username FROM user_info"
			cur.execute(sql_query) # executes the SQL request to the database.
			for row in cur.fetchall(): # This retrieves the return information from the database.
				list =['calories', 'fat', 'carbs', 'protein', 'activity'] #This is a list of all the columns in the food_micro table to be reset.

				#This looks through the list setting each value to zero.
				for type in list:
					insert_query = "UPDATE food_micro SET " + type + " = %s WHERE username = %s" #SQL statement to overwite the values on the database to 0.
					cur.execute(insert_query, (float(0), row[0],)) #This executes the SQL statement.
					db.commit() #This commits the executed SQL statement to the database.

				meal_column = ['breakfast', 'lunch', 'dinner', 'snacks'] #This is a list of all the columns in the meal table to be reset.

				#This looks through the list setting each value to NULL.
				for meal in meal_column:
					insert_query = "UPDATE meal SET " + meal + " = NULL WHERE username = %s" #SQL statement to overwite the values on the database to NULL.
					cur.execute(insert_query, (row[0],)) #This executes the SQL statement.
					db.commit() #This commits the executed SQL statement to the database.
			return "Daily reset completed successfully", 200 #successful status code.


		else:
			print('Daily reset is activate...') #Show the admin that the process has been started.
			try:
				while True: # This is an infinite loop for a thread so the database can reset around midnight every day.
					t = time() #Time library.
					sleep(1800) # Check the time every 30 minutes.
					hour_time = ctime(t).split(" ")[3].split(":")[0] # Gets the local time and finds just the hour it is in that particular time zone.
					if int(hour_time) == 00: #If the local time is midnight reset the database NULL.
						sql_query = "SELECT username FROM user_info"
						cur.execute(sql_query) # executes the SQL request to the database.
						for row in cur.fetchall(): # This retrieves the return information from the database.
							list =['calories', 'fat', 'carbs', 'protein', 'activity'] #This is a list of all the columns in the food_micro table to be reset.

							#This looks through the list setting each value to zero.
							for type in list:
								insert_query = "UPDATE food_micro SET " + type + " = %s WHERE username = %s"
								cur.execute(insert_query, (float(0), row[0],))  #This executes the SQL statement.
								db.commit() #This commits the executed SQL statement to the database.

							meal_column = ['breakfast', 'lunch', 'dinner', 'snacks'] #This is a list of all the columns in the meal table to be reset.

							#This looks through the list setting each value to NULL.
							for meal in meal_column:
								insert_query = "UPDATE meal SET " + meal + " = NULL WHERE username = %s"
								cur.execute(insert_query, (row[0],))  #This executes the SQL statement.
								db.commit() #This commits the executed SQL statement to the database.

				return "Daily reset completed successfully", 200 #successful status code.

			except Exception as e:
				return "An error has occured, please try again", 400 #Failure status code.


@application.route("/retrieve_info", methods=["POST", "GET"]) #This allows for POST and GET requests to be sent and received respectively.
def retrieve_info():

	fetchdata = None

	# If the method we receive is a POST.
	if request.method == "POST":
		info = request.form # This is where the information is received from the sent POST request.
		username = info["Username"]
		type = info["Type"] # calories, fat, carbs, protein, activity
		#if the type is not equal to None then make sure all the letters are lowercase.
		if type != None:
			type = type.lower() # This converts the string to lowercase characters.

		#If the username or type input is black return an error status code.
		if username == '' or type == '':
			return "username, value, type, and action cannot be blank", 400 #Failure status code.

		#If type is equal to caloies, fat, carbs, protein, or activity then set the table we retireve information from to food_micro.
		elif type == 'calories' or type == 'fat' or type == 'carbs' or type == 'protein' or type == 'activity':
			table = 'food_micro'  #Table we will retrieve information from.

		#If type is equal to breakfast, lunch, dinner, snacks the set the table we will retrieve the infomation from to meal.
		elif type == 'breakfast' or type == 'lunch' or type == 'dinner' or type == 'snacks':
			table = 'meal' #We will retrieve information from meal.

		#Creates a connection with the database
		con = db
		cur = db.cursor()

		#Make a request to the database
		sql_query = "SELECT " + type + " FROM " + table + " where username = %s"
		cur.execute(sql_query, (username,)) # This executes the SQL statement
		for row in cur.fetchall(): # This retrieves the return information from the database.
			micro = row[0]

		return str(micro), 200 #successful status code.




if __name__ == "__main__":
	application.run(debug=True, port=8081, threaded=True)

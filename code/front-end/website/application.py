from flask import Flask, render_template, request, redirect, url_for, Response, jsonify
import csv, json
import requests
from werkzeug.security import generate_password_hash

#Initates the Flask server.
application = Flask(__name__)


#Home page end point.
@application.route("/")
def home():
    return render_template("index.html") #Opens the homepage.

################################################################################


#This endpoint allows users to register for an account where they will then be taken to their user page if all completed successfully.
@application.route("/web_register", methods=["POST", "GET"]) # Allows POST and GET requests to be received.
def web_register():
    if request.method == "POST": #If the request is a POST rrequest execute the code below.
        username = request.form["Name"] #This is where the username will be stored from the recieved POST request.
        password = request.form["Password"] #This is where the password will be recieved from the POST request.

        #request send to the Flask server to create an account on the database.
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})

        if r.status_code == 200: #If the returned status code is a success 200 status code then execute the code below.
            activity = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'activity'}).content)[2:-1] # This sents a requst to the Flask server to get the activity from the database.
            calories = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'calories'}).content)[2:-1] # This sents a requst to the Flask server to get the calories from the database.
            carbs = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'carbs'}).content)[2:-1] # This sents a requst to the Flask server to get the carbs from the database.
            protein = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'protein'}).content)[2:-1] # This sents a requst to the Flask server to get the protein from the database.
            fat = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'fat'}).content)[2:-1] # This sents a requst to the Flask server to get the fat from the database.
            breakfast = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'breakfast'}).content)[2:-1].split("|") # This sends a request to the Flask API Server to get breakfast information for the inputed username and password. This information is then split into a list.

            breakfast_foods = [] #This contains all the foods from the information on the database.
            breakfast_calories = [] #This contains all the calories from the information on the database. #This contains all the calories from the breakfast information on the database.

            #If the the database returns no information on breakfast then return ['None'] for breakfast_calories and breakfast_foods.
            if (breakfast == ['None']): #If the database is empty for the section requested.
                breakfast_foods = ["None"] # This makes the variable a none in a list as there is no info in the database.
                breakfast_calories = ["None"] #This is set to None in a list if there is no food in the database.


            lunch = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'lunch'}).content)[2:-1].split("|") #This sends a requst to the Flask API Server to get lunch information from the database.
            lunch_foods = [] #This contains all the foods from the information on the database.
            lunch_calories = [] #This contains all the calories from the information on the database.
            if (lunch == ['None']): #If the database is empty for the section requested.
                lunch_foods = ["None"] # This is set to None in a list if there is no food in the database.
                lunch_calories = ["None"] #This is set to None in a list if there is no food in the database.

            dinner = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'dinner'}).content)[2:-1].split("|") #This sends a requst to the Flask API Server to get snack information from the database.
            dinner_foods = [] #This contains all the foods from the information on the database.
            dinner_calories = [] #This contains all the calories from the information on the database.
            if (dinner == ['None']): #If the database is empty for the section requested.
                dinner_foods = ["None"] # This is set to None in a list if there is no food in the database.
                dinner_calories = ["None"] #This is set to None in a list if there is no food in the database.


            snacks = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'snacks'}).content)[2:-2].split("|")####################################################################################################
            snacks_foods = [] #This contains all the foods from the information on the database.
            snacks_calories = [] #This contains all the calories from the information on the database.
            if (snacks == ['Non']): #If their is not information in the breakfast database.
                snacks_foods = ["None"] # This is set to None in a list if there is no food in the database.
                snacks_calories = ["None"] #This is set to None in a list if there is no food in the database.

            #This passes the user information to the user page dashboard to be displayed to the user.
            return render_template("user.html", Activity=activity, Calories=calories, Carbs=carbs, Protein=protein, Fat=fat, breakfast_len=len(breakfast_foods), breakfast_foods=breakfast_foods, breakfast_calories=breakfast_calories, lunch_len=len(lunch_foods), lunch_foods=lunch_foods, lunch_calories=lunch_calories, dinner_len=len(dinner_foods), dinner_foods=dinner_foods, dinner_calories=dinner_calories, snacks_len=len(snacks_foods), snacks_foods=snacks_foods, snacks_calories=snacks_calories)

        else: #If there is an error logging in  then go to the error page.
            return render_template("error_register.html", data=str(r.content)[2:-1])

    else: #If a POST request is not detected then go to the registation page.
        return render_template("register.html")

@application.route("/web_login", methods=["POST", "GET"]) # Allows POST and GET requests to be received.
def web_login():
    if request.method == "POST": #If the request is a POST rrequest execute the code below.
        username = request.form["Name"] #This is where the username will be stored from the recieved POST request.
        password = request.form["Password"] #This is where the password will be recieved from the POST request.

        #request to login using the Flask API.
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})

        if r.status_code == 200: #If the returned status code is a success 200 status code then execute the code below.
            activity = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'activity'}).content)[2:-1] # This sents a requst to the Flask server to get the activity from the database.
            calories = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'calories'}).content)[2:-1] # This sents a requst to the Flask server to get the calories from the database.
            carbs = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'carbs'}).content)[2:-1] # This sents a requst to the Flask server to get the carbs from the database.
            protein = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'protein'}).content)[2:-1] # This sents a requst to the Flask server to get the protein from the database.
            fat = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username,'Type': 'fat'}).content)[2:-1] # This sents a requst to the Flask server to get the fat from the database.
            breakfast = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'breakfast'}).content)[2:-2].split("|")


            breakfast_foods = [] #This contains all the foods from the information on the database.
            breakfast_calories = [] #This contains all the calories from the information on the database.
            if (breakfast == ['Non']): #If their is not information in the breakfast database.
                breakfast_foods = ["None"] # This makes the variable a none in a list as there is no info in the breakfast table of database.
                breakfast_calories = ["None"] #This is set to None in a list if there is no food in the database. # This makes the variable a none in a list as there is no info in the breakfast table of the database.


            #If their is information in the database then we have to deserialise the information from the table and add them to their respective calories and food list.
            else:
                for meal in breakfast:
                    meal = meal.split("%") #This deserialises the string from the database into lists of lists.
                    breakfast_foods.append(meal[0]) # This adds the meal to the food list so it can be displayed on the user dashboard under the food section.
                    breakfast_calories.append(meal[2]) #This adds all the calories of the foods retrieved from the database to the list above.

            lunch = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'lunch'}).content)[2:-2].split("|")
            lunch_foods = [] #This contains all the foods from the information on the database.
            lunch_calories = [] #This contains all the calories from the information on the database.
            if (lunch == ['Non']): #If their is not information in the breakfast database.
                lunch_foods = ["None"]
                lunch_calories = ["None"] #This is set to None in a list if there is no food in the database.

            #If their is information in the database then we have to deserialise the information from the table and add them to their respective calories and food list.
            else:
                for meal in lunch:
                    meal = meal.split("%") #This deserialises the string from the database into lists of lists.
                    lunch_foods.append(meal[0]) # This adds the meal to the food list so it can be displayed on the user dashboard under the food section.
                    lunch_calories.append(meal[2]) #This adds all the calories of the foods retrieved from the database to the list above.

            dinner = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'dinner'}).content)[2:-2].split("|")
            dinner_foods = [] #This contains all the foods from the information on the database.
            dinner_calories = [] #This contains all the calories from the information on the database.
            if (dinner == ['Non']): #If their is not information in the breakfast database.
                dinner_foods = ["None"]
                dinner_calories = ["None"] #This is set to None in a list if there is no food in the database.

            #If their is information in the database then we have to deserialise the information from the table and add them to their respective calories and food list.
            else:
                for meal in dinner:
                    meal = meal.split("%") #This deserialises the string from the database into lists of lists.
                    dinner_foods.append(meal[0]) # This adds the meal to the food list so it can be displayed on the user dashboard under the food section.
                    dinner_calories.append(meal[2]) #This adds all the calories of the foods retrieved from the database to the list above.

            snacks = str(requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : 'snacks'}).content)[2:-2].split("|")
            snacks_foods = [] #This contains all the foods from the information on the database.
            snacks_calories = [] #This contains all the calories from the information on the database.
            if (snacks == ['Non']): #If their is not information in the breakfast database.
                snacks_foods = ["None"]
                snacks_calories = ["None"] #This is set to None in a list if there is no food in the database.

            #If their is information in the database then we have to deserialise the information from the table and add them to their respective calories and food list.
            else:
                for meal in snacks:
                    meal = meal.split("%") #This deserialises the string from the database into lists of lists.
                    snacks_foods.append(meal[0]) # This adds the meal to the food list so it can be displayed on the user dashboard under the food section.
                    snacks_calories.append(meal[2]) #This adds all the calories of the foods retrieved from the database to the list above.


            #This passes the user information to the user page dashboard to be displayed to the user.
            return render_template("user.html", Activity=activity, Calories=calories, Carbs=carbs, Protein=protein, Fat=fat, breakfast_len=len(breakfast_foods), breakfast_foods=breakfast_foods, breakfast_calories=breakfast_calories, lunch_len=len(lunch_foods), lunch_foods=lunch_foods, lunch_calories=lunch_calories, dinner_len=len(dinner_foods), dinner_foods=dinner_foods, dinner_calories=dinner_calories, snacks_len=len(snacks_foods), snacks_foods=snacks_foods, snacks_calories=snacks_calories)

        else: #If an error has occurred when logging in then pass the error to the page to notify the user.
            return render_template("error_login.html", data="Incorrect username or password, please try again") #This is sending the error message to the page.

    else: #If no POST request detected then open the login page.
        return render_template("login.html")


@application.route("/web_delete", methods=["POST", "GET"]) # Allows POST and GET requests to be received.
def web_delete():

    if request.method == "POST": #If the request is a POST rrequest execute the code below.
        username = request.form["Name"] #This is where the username will be stored from the recieved POST request.
        password = request.form["Password"] #This is where the password will be recieved from the POST request.

        #request to login
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/delete_account', data={'Username' : username,'Password': password})

        if r.status_code == 200: #If the returned status code is a success 200 status code then execute the code below.
            return render_template("delete_page_success.html")

        else: #If an error has occurred deleting the account.
            return render_template("error_delete_account.html",  data="Incorrect username or password, please try again") #This is an error login page if the status code is not 200.

    else: #Otherwise no POST request then open the delete account page.
        return render_template("delete_page.html") # Open the delete account page.








if __name__ == "__main__":
    application.run(debug=True, host="0.0.0.0", port=8082)
################################################################################

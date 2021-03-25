

import requests, time
import unittest

class FlaskTestCases(unittest.TestCase):

# /LOGIN API
    #Test for user in the database.
    def test_correct_login(self):
        username = 'kev_co'
        password = 'BortasCogan2020'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 200)

    #Test for a username not in the database.
    def test_failure_username_login(self):
        username = 'kev_'
        password = 'BortasCogan2020'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    #Test for a password not in the database.
    def test_failure_password_login(self):
        username = 'kev_co'
        password = 'Bort'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    #Test for both username and password not in the database.
    def test_failure_username_password_login(self):
        username = 'kev'
        password = 'Bort'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    def test_failure_both_blank_login(self):
        username = ''
        password = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    def test_failure_username_blank_login(self):
        username = ''
        password = 'BortasCogan2020'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    def test_failure_password_blank_login(self):
        username = 'kev_co'
        password = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

#REGISTER API

    #Test for user already registered.
    def test_already_register(self):
        username = 'kev_co'
        password = 'BortasCogan2020'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    #Test for a username not in the database.
    def test_correct_register(self):
        username = 'kev_'
        password = 'BortasCogan2020'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 200)
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/delete_account', data={'Username' : username})

    #Test for a blank username and password.
    def test_failure_password_register(self):
        username = ''
        password = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    #Test for a blank username.
    def test_blank_username_register(self):
        username = ''
        password = 'Bort'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

    #Test for blank password
    def test_blank_password_register(self):
        username = 'kev_co'
        password = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})
        self.assertEqual(r.status_code, 400)

#DELETE USER
    #Test delete
    def test_correct_delete(self):
        username = 'Bill_Clinton'
        password = 'President'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register', data={'Username' : username,'Password': password})
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/delete_account', data={'Username' : username})
        self.assertEqual(r.status_code, 200)

    #Test delete failure - username not in database
    def test_failure_usename(self):
        username = 'Bill_Clinton'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/delete_account', data={'Username' : username})
        self.assertEqual(r.status_code, 400)

    #Test no username input - failure
    def test_failure_blank_username(self):
        username = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/delete_account', data={'Username' : username})
        self.assertEqual(r.status_code, 400)

#UPDATE_MICRO

    #Test correct username and added to carbs
    def test_correct_username_update_carbs_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'carbs'
        action = 'add'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)
        action = 'delete'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

    #Test correct username and added protein
    def test_correct_username_update_protein_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'protein'
        action = 'add'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)
        action = 'delete'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

    #Test correct username and added fat
    def test_correct_username_update_fat_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'fat'
        action = 'add'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)
        action = 'delete'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

    #Test correct username and added calories
    def test_correct_username_update_calories_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'calories'
        action = 'add'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)
        action = 'delete'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

####
    #Test correct username and delete to carbs
    def test_correct_username_delete_carbs_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'carbs'

        action = 'add'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)

    #Test correct username and delete protein
    def test_correct_username_delete_protein_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'protein'

        action = 'add'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)

    #Test correct username and delete fat
    def test_correct_username_delete_fat_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'fat'

        action = 'add'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)

    #Test correct username and delete calories
    def test_correct_username_delete_calories_micro(self):
        username = 'kev_co'
        value = '1'
        type = 'calories'

        action = 'add'
        requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})

        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 200)


    #Test negative value input
    def test_negative_input(self):
        username = 'kev_co'
        value = '-1'
        type = 'calories'
        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 400)

    #Test all blank inputs
    def test_all_blank_inputs(self):
        username = ''
        value = ''
        type = ''
        action = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro', data={'Username' : username,'Value': value, 'Type' : type, 'Action' : action})
        self.assertEqual(r.status_code, 400)

#UPDATE FOOD
    #Test update with all blank values.
    def test_update_food_all_blank(self):
        username = ''
        type = ''
        action = ''
        food = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 400)

    #Test with breakfast blank.
    def test_update_food_username_blank(self):
        username = ''
        type = 'breakfast'
        action = 'add'
        food = 'chips'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 400)

    #Test with type value as blank.
    def test_update_food_type_blank(self):
        username = 'kev_co'
        type = ''
        action = 'add'
        food = 'chips'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 400)

    #Test the action values as blank.
    def test_update_food_action_blank(self):
        username = 'kev_co'
        type = 'breakfast'
        action = ''
        food = 'chips'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 400)

    #Test the food values as blank.
    def test_update_food_blank(self):
        username = 'kev_co'
        type = 'breakfast'
        action = 'add'
        food = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 200)

    #Test a correct food add.
    def test_correct_add_food_update(self):
        username = 'kev_co'
        type = 'breakfast'
        action = 'add'
        food = 'chips'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 200)
        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})

    #Test a correct food delete.
    def test_correct_delete_food_update(self):
        username = 'kev_co'
        type = 'breakfast'
        action = 'add'
        food = 'chips'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 200)

    #Test a correct food overwrite.
    def test_correct_overwrite_food_update(self):
        username = 'kev_co'
        type = 'breakfast'
        action = 'add'
        food = 'chips'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        action = 'overwrite'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        action = 'delete'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food', data={'Username' : username, 'Type' : type, 'Action' : action, 'Food': food})
        self.assertEqual(r.status_code, 200)

#RETRIEVE INFO
    #Test success call for calories
    def test_retrieve_calories(self):
        username = 'kev_co'
        type = 'calories'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 200)

    #Test success call for activity
    def test_retrieve_activity(self):
        username = 'kev_co'
        type = 'activity'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 200)

    #Test success call for carbs
    def test_retrieve_carbs(self):
        username = 'kev_co'
        type = 'carbs'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 200)

    #Test success call for protein
    def test_retrieve_protein(self):
        username = 'kev_co'
        type = 'protein'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 200)

    #Test success call for fat
    def test_retrieve_fat(self):
        username = 'kev_co'
        type = 'fat'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 200)

    #Test blank username
    def test_retrieve_with_blank_username(self):
        username = ''
        type = 'fat'
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 400)

    #Test blank type
    def test_retrieve_with_blank_type(self):
        username = 'kev_co'
        type = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 400)

    #Test both blank
    def test_retrieve_with_blank(self):
        username = ''
        type = ''
        r = requests.post('http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info', data={'Username' : username, 'Type' : type})
        self.assertEqual(r.status_code, 400)



if __name__ == '__main__':
    unittest.main()

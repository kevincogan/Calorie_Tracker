#Alternatively, this code can be easily run in the Google Collab programming environment copy and paste the link into your browser to access and run the code (note please take away the  '#' at the top of the link when copying the code to the internet browser).
# https://colab.research.google.com/drive/1xqqB4PdPVrYWuwqtFL8oM5SKyMopo9YQ



import tensorflow as tf
import tensorflow_hub as hub
import tensorflow_datasets as tfds
import matplotlib.pylab as plt
import os
import numpy as np
from google.colab import files


#This stories all the training information on the model such as a list of values for training accuracy and loss of each batch so that we can graph this information and make more informed decisions based off the graphs
class BatchInfo(tf.keras.callbacks.Callback):

    def __init__(self):
        self.losses = [] #This is a list of training losses.
        self.accuracy = [] #This is a list of training accuracies.

    def on_train_batch_end(self, batch, logs=None):
        self.losses.append(logs['loss']) #Retreives the loss infomation from the callback function when the model is being trained.
        self.accuracy.append(logs['acc']) #Retreives the accuracy infomation from the callback function when the model is being trained.
        self.model.reset_metrics() #This resets the model from the cuurent state that it is in.


#This automatically exports the model in a zip file to be save locally on you PC.
def export_model(model):

    !mkdir -p saved_model #This make a directory called 'saved_model'
    model.save('saved_model/food_identify_model') # This is the path of ther the model will be saved.

    !zip -r /content/saved_model.zip /content/saved_model # This will zip the contents of the file.
    files.download("/content/saved_model.zip") # This will download the zipfile with the model onto your PC.



# Thsi reformates the image into a format that is accepted by the neural network.
def Reformatter(image, label):

    image = tf.cast(image, tf.float32) # tf.cast changes the datatype into a form that can be passed into our neural network.

    image /= 255.0 # Reformating the image by dividing 255, so the coloured pixels will be between 1 and 0.

    return tf.image.resize(image, [224, 224]), tf.one_hot(label, 101) # This returns the scaled image above into the same format as the images passed into mobilenet_v2 model when it was orginally created.



def main():


    #This imports the 'food101' dataset from Tensorflow and only take the first 10% of the dataset for training. The as_supervised=True will create a tuple with the image and the label of the image. with_info will provide more infomation about the labels and the dataset which is good to get an understanding of the data.
    total_ds, info = tfds.load(name='food101', with_info=True, as_supervised=True, split=['train[:10%]', 'validation[:10%]'])
    print(info)

    # total_ds provides us with a list with two image objecs, the training and validation datasets, we need to use numpy.concatenate() to combine the dataset and the shuffles the images randomly so we can the divide the images into a format that suit our method.
    total_ds=total_ds[0].concatenate(total_ds[1])

    total_ds=total_ds.shuffle(3000)

    #This manually splits the dataset into a training, validation, and testing dataset.
    total_val=total_ds.take(600) #This takes the first 600 images in the dataset.
    total_test_temp=total_ds.skip(600) #We have to take the remaining images and save them into a temporary variable so we can then divide the images up more.
    total_test=total_test_temp.take(400) # This takes the first 400 images from 'total_test_temp' and save them as total_test.
    total_train=total_test_temp.skip(400) #This then takes the remaining image and saves them into the training dataset for the neural network.

    # The batch size is set to 64.
    batch_size = 64

    #Scales and shiffles the images and applys the batch size on the images. This is applied to the training, validation, and test dataset.
    train_ds = total_train.map(Reformatter).shuffle(1900).batch(batch_size)
    test_ds = total_test.map(Reformatter).batch(batch_size)
    validation_ds = total_val.map(Reformatter).batch(batch_size)


    #Saving the Mobilenet_v2 preterained model download link from tensorflow hub as a variable.
    mobilenet = "https://tfhub.dev/google/tf2-preview/mobilenet_v2/feature_vector/4"

    #This imports the mobilenet_v2 from tensorflowhub and then resizes the images of the Mobilenet_v2 to ensure the images are of (224, 224,3).
    mobilenet_layer = hub.KerasLayer(mobilenet, input_shape=(224,224,3))


    #This means that we do not want to train the top layers of the model.
    mobilenet_layer.trainable = False

    #Build the model using the pretrained model by adding new layers from the food image we are passing through.
    model = tf.keras.Sequential([
        mobilenet_layer,
        tf.keras.layers.Dropout(0.5),
        tf.keras.layers.Dense(101, activation= 'softmax'),])

    model.summary() #Gives us a summary of the structure of the neural network.


    #Compiled the model using the 'adam' optimizer and sets a fuction on how we will monitor the losses and accuracy.
    model.compile(
        optimizer=tf.keras.optimizers.Adam(), #This optimizer is know to provide good results for image classification.
        loss=tf.losses.CategoricalCrossentropy(from_logits=True), #This is used to define a loss of the training and optimisation process, in other words this acts as a cost function.
        metrics=['acc']) # This allows us to check the accuracy of the model during the training process.

    #Calling the method BatchInfo().
    batch_info = BatchInfo()

    #Begins training the model with the training dataset and validating each epoch with the validation dataset. In this case we ran the model for 250 epoches however when graphed the optimal number of epoch was arounf 50 - 80 epoches. Despite this we decided to use 200 epoches as we got an extra 3% before we were going to export the model to try get the best accuracy possible for our users.
    history = model.fit_generator(train_ds, epochs= 10, validation_data=validation_ds, callbacks = [batch_info])


    #Graph the loss of the model as the model is being trained.
    plt.figure()
    plt.ylabel("Loss")
    plt.xlabel("Training Steps")
    plt.ylim([0,2])
    plt.plot(batch_info.losses)


    #Graph the accuracy of the model as it is being trained.
    plt.figure()
    plt.ylabel("Accuracy")
    plt.xlabel("Training Steps")
    plt.ylim([0,1])
    plt.plot(batch_info.accuracy)


    #Evaluated the model accuracy by using the unique test dataset.
    result=model.evaluate(test_ds)


    #Automated testing of the model by comparing the image, actual label, and predicted label.
    for test_data in total_test.take(100):

        # Image is int8
        image, label = test_data[0], test_data[1]
        new_image, label_arr = Reformatter(test_data[0], test_data[1])

        # Numpy using expand_dims as float32
        new_image = np.expand_dims(new_image, axis=0)

        img = tf.keras.preprocessing.image.img_to_array(image)
        pred=model.predict(new_image) # gets the predicted labels for the image.

        #Displays the image on the computer
        plt.figure()
        plt.imshow(image)
        plt.show()
        print('Correct label: %s' % info.features['label'].names[label.numpy()]) #Displays the actual label for the image.
        print('Predicted label: %s' % info.features['label'].names[np.argmax(pred)]) #Displays the predicted label for the image.


    #Exports the model to the PC in a zipfile format.
    export_model(model)

if __name__ == "__main__":
    main()

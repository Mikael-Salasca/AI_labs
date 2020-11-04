import numpy as np
import matplotlib.pyplot as plt

b_size = [1, 10, 100, 1000, 60000]
time = [98, 9, 2, 1, 0]
accuracy = [0.8138, 0.8254, 0.8201, 0.7302, 0.1107]

plt.plot(b_size, time)
plt.xlabel('Batch Size')
plt.ylabel('Time')
plt.title('Time in relation to Batch Size')
plt.show()

plt.plot(b_size, accuracy)
plt.xlabel('Batch Size')
plt.ylabel('Accuracy')
plt.title('Accuracy in relation to Batch Size')
plt.show()

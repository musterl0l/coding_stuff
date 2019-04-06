
from numpy import arange, sin, pi
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.figure import Figure
import tkinter as Tk
from tkinter import ttk
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation

def openfile():
    print("Clicked")

root = Tk.Tk()
frame = Tk.Frame(root)


root.geometry("1400x800")
root.resizable(0,0)

openButton = Tk.Button(root, text="Open File", command = openfile)
#openButton.pack(side=tk.TOP)
openButton.place(x=100,y = 500)


#otherButton = Tk.Button(root, text="Open File", command = openfile)
#openButton.pack(side=tk.TOP)
#otherButton.pack(anchor=Tk.NW)










Tk.mainloop()



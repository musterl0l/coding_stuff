#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from six.moves import tkinter as Tk

from matplotlib.backends.backend_tkagg import (
    FigureCanvasTkAgg, NavigationToolbar2TkAgg)
# Implement the default Matplotlib key bindings.
from matplotlib.backend_bases import key_press_handler
from matplotlib.figure import Figure
from tkinter import filedialog
from PIL import ImageTk, Image

import numpy as np

def fileOpen():
    fileName = filedialog.askopenfilename(initialdir = "/",title = "Select file",filetypes = (("jpeg files","*.jpg"),("all files","*.*")))
    print (fileName)
    
    img = ImageTk.PhotoImage(Image.open(fileName))    

    panel = Tk.Label(root, image = img)
    panel.img = img
    panel.grid(row = 1, column = 1)
    #panel.place(x = 10, y = 50)
    #panel.pack(side = "bottom", fill = "both", expand = "yes")
    # EEG Plot

def updateUI():
    canvas.draw()
    #canvas2.draw()
    

root = Tk.Tk()
root.wm_title("BCI Project")

#root.geometry("1400x700")

panel = Tk.Label(root, text = "empty label")
panel.grid(row = 0, column =  2)



fig = Figure(figsize = (6,4),dpi=100)
t = np.arange(0, 3, .01)
fig.add_subplot(111).plot(t, 2 * np.sin(2 * np.pi * t))

canvas = FigureCanvasTkAgg(fig, master=root)  # A tk.DrawingArea.
#canvas.get_tk_widget().pack(side=Tk.TOP, fill=Tk.BOTH, expand=1)
canvas.get_tk_widget().grid(row = 2, column = 1, sticky = "W")
canvas.draw()


#fig2 = Figure(figsize = (6,4),dpi=100)
#t2 = np.arange(0, 3, .01)
#fig2.add_subplot(111).plot(t2, 2 * np.cos(2 * np.pi * t2))

#canvas2 = FigureCanvasTkAgg(fig2, master=root)  # A tk.DrawingArea.
#canvas2.draw()
#canvas.get_tk_widget().pack(side=Tk.TOP, fill=Tk.BOTH, expand=1)
#canvas2.get_tk_widget().grid(row = 2, column = 2)


#toolbar = NavigationToolbar2TkAgg(canvas, root)
#toolbar.update()
#canvas._tkcanvas.pack(side=Tk.TOP, fill=Tk.BOTH, expand=1)


#def on_key_press(event):
#    print("you pressed {}".format(event.key))
#    key_press_handler(event, canvas, toolbar)


#canvas.mpl_connect("key_press_event", on_key_press)


def _quit():
    root.quit()     # stops mainloop
    root.destroy()  # this is necessary on Windows to prevent
                    # Fatal Python Error: PyEval_RestoreThread: NULL tstate


button = Tk.Button(master=root, text="Open", command=fileOpen)
button.grid(row = 0, column = 1)
button2 = Tk.Button(master=root, text=" ", command=updateUI)
button2.grid(row = 0, column = 3)



#button.place(x = 100, y = 10)

Tk.mainloop()
import threading
import time
import queue

globale_thread_kontrolle = 1                #falls 0 werden alle threads beendet
block_thread_list = 0                       #0 darf zugriff, sichert ab das die liste nicht vermischt wird
    
class message(object):                      #messages als klasse um zu wissen ob input oder output
    def __init__(self, in_out_flag, message):
        self.message_buffer = message
        self.in_out = in_out_flag           #0 wenn interne message, wenn 2 dann ein fehler

class in_out_queue(object):                 #die jewailige queue mit id damit sie an richtiger stelle in der liste ist
    def __init__(self, ID):
        self.queue_ID = ID
        self.blocked = 0
        self.inbox = queue.Queue()
        
class mess_thread(threading.Thread):                      #thread klasse
    def __init__(self, ID):
        threading.Thread.__init__(self)
        self.thread_ID = ID
        self.history = []
        
    def run(self):
        self.inbox = in_out_queue(self.thread_ID)
        while globale_thread_kontrolle == 1:            #schleife die in/out put haendelt
            if (self.inbox.inbox.empty() == False):
                print("inbox action")
                tmp_mess = self.inbox.inbox.get()
                if(tmp_mess.in_out == 1):
                    sequencer_queue.put(tmp_mess)
                else:                                   #übergiebt externe messages an den sequencer um die eigen queue konsistent zu halten
                    self.history.append(tmp_mess)
                    print("his")
                
        f = open ('logfile_thread_' + str(self.thread_ID), 'w')  #logfile anlegen
        i=0
        for entry in self.history:
            f.write('message '+str(i)+': '+str(entry.message_buffer)+' thread ID: '+str(entry)+'\n')
            i+=1
        f.close()

class sequencer(threading.Thread):                  #sequenzer thread
    def __init__(self):
        threading.Thread.__init__(self)
        
    def run(self):
        while (globale_thread_kontrolle == 1):        #sequenzer der nachrichten empfaengt und weiterleitet
            if(sequencer_queue.empty() == False):
                print("sequencer action")
                mess = sequencer_queue.get()
                mess.in_out = 0
                for y in threads:          #verteilt die nachricht an alle queues der threads weiter
                    y.inbox.inbox.put(mess)

def generateMsg(n, t):
    for i in range(n):
        x = random.randint(0,9999)
        multi_thread[random.randint(0,len(t)-1)].q.put(x)                          #send to random thread in list t
        

anzahl_threads = eval(input("Bitte anzahl der erwuenschten Threads eingeben"))
threads = []

for i in range (0, anzahl_threads):         #erstellt die n bestellten threads und startet sie
    multi_thread = mess_thread(i)
    multi_thread.start()
    threads.append(multi_thread)            #parkt thread in liste

sequencer_queue = queue.Queue()
sequencer_thread = sequencer()              #sequencer starten
sequencer_thread.start()

for h in range (0, 15):
    new_mess = message(0, "i am a message")
    sequencer_queue.put(new_mess)

time.sleep(10)
globale_thread_kontrolle = 0

for x in threads:                           #abwarten biss alle threads fertig sind
    x.join()
    
print("BY BY")

import ftplib
import os;

filename = "mysample.png"
ftp = ftplib.FTP()
ftp.connect("sbkang.synology.me", 21)    #Ftp 주소 Connect(주소 , 포트)
ftp.login("kdh314", "GunOf9949!")         #login (ID, Password)
ftp.cwd("/kdh314/")   #파일 전송할 Ftp 주소 (받을 주소)
os.chdir(r"./") #파일 전송 대상의 주소(보내는 주소)
myfile = open(filename, 'rb')       #Open( ~ ,'r') <= Text파일은 됨, Open( ~ ,'rb') <= 이미지파일 됨
ftp.storbinary('STOR ' + filename, myfile)

myfile.close()
ftp.close() 


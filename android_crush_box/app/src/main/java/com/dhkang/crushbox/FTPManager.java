package com.dhkang.crushbox;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FTPManager {
    private final String TAG = "FTP";
    public FTPClient ftp_client_ = null;

    public FTPManager() {
        ftp_client_ = new FTPClient();
    }

    public boolean connect(String host, String username, String password, int port) {
        boolean result = false;
        try{
            ftp_client_.connect(host, port);

            if(FTPReply.isPositiveCompletion(ftp_client_.getReplyCode())) {
                result = ftp_client_.login(username, password);
                ftp_client_.enterLocalPassiveMode();
            }
        }catch (Exception e){
            Log.d(TAG, "Couldn't connect to host");
        }
        return result;
    }

    public boolean disconnect() {
        boolean result = false;
        try {
            ftp_client_.logout();
            ftp_client_.disconnect();
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "Failed to disconnect with server");
        }
        return result;
    }

    public boolean download(String srcFilePath, String desFilePath) {
        boolean result = false;
        try{
            ftp_client_.setFileType(FTP.BINARY_FILE_TYPE);
            ftp_client_.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            FileOutputStream fos = new FileOutputStream(desFilePath);
            result = ftp_client_.retrieveFile(srcFilePath, fos);
            fos.close();
        } catch (Exception e){
            Log.d(TAG, "Download failed");
        }
        return result;
    }

    public String[] get_file_list(String directory) {
        String[] fileList = null;
        int i = 0;
        try {
            FTPFile[] ftpFiles = ftp_client_.listFiles(directory);
            fileList = new String[ftpFiles.length];
            for(FTPFile file : ftpFiles) {
                String fileName = file.getName();

                if (file.isFile()) {
                    fileList[i] = "(File) " + fileName;
                } else {
                    fileList[i] = "(Directory) " + fileName;
                }

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public String get_current_directory(){
        String directory = null;
        try{
            directory = ftp_client_.printWorkingDirectory();
        } catch (Exception e){
            Log.d(TAG, "Couldn't get current directory");
        }
        return directory;
    }

    public boolean change_directory(String directory) {
        try{
            ftp_client_.changeWorkingDirectory(directory);
            return true;
        }catch (Exception e){
            Log.d(TAG, "Couldn't change the directory");
        }
        return false;
    }


    public boolean create_directory(String directory) {
        boolean result = false;
        try {
            result =  ftp_client_.makeDirectory(directory);
        } catch (Exception e){
            Log.d(TAG, "Couldn't make the directory");
        }
        return result;
    }

    public boolean delete_directory(String directory) {
        boolean result = false;
        try {
            result = ftp_client_.removeDirectory(directory);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't remove directory");
        }
        return result;
    }

    public boolean delete(String file) {
        boolean result = false;
        try{
            result = ftp_client_.deleteFile(file);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't remove the file");
        }
        return result;
    }

    public boolean rename(String from, String to) {
        boolean result = false;
        try {
            result = ftp_client_.rename(from, to);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't rename file");
        }
        return result;
    }


    public boolean upload(String srcFilePath, String desFileName, String desDirectory) {
        boolean result = false;
        try {
            FileInputStream fis = new FileInputStream(srcFilePath);
            if(change_directory(desDirectory)) {
                result = ftp_client_.storeFile(desFileName, fis);
            }
            fis.close();
        } catch(Exception e){
            Log.d(TAG, "Couldn't upload the file");
        }
        return result;
    }
}

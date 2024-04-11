package com.lhqjlb.project.BaiDuDemo;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Chat chat = new Chat();
        boolean result = chat.getAccessToken();
        if (result){
            Scanner scanner = new Scanner(System.in);
            String question = scanner.nextLine();
            while(!"q".equals(question)){
                chat.getAnswer(question);
                chat.getRequestBody();
                question = scanner.nextLine();
            }
        }
    }
}
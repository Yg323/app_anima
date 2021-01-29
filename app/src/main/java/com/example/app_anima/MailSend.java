package com.example.app_anima;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class MailSend extends AppCompatActivity {
    private static final String USER = "anima_ssp@naver.com";
    private static final String PASSWORD = "gnucs12!@";
    private static final String TITLE = "[ANIMA] 비밀번호 찾기 인증코드 메일입니다.";

    private static String authCode = MailSender.createEmailCode();
    public static String getAuthCode() {
        return authCode;
    }

    public void sendMail(Context context, String email) {
        try {
            MailSender mailSender = new MailSender(USER, PASSWORD);
            mailSender.sendMail(TITLE, authCode, email);
            Toast.makeText(context, "이메일로 인증코드를 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            e.printStackTrace();
            Toast.makeText(context, "이메일 형식이 잘못되었습니다!", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            e.printStackTrace();
            Toast.makeText(context, "인터넷 연결을 확인해주세요!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "이메일 전송 실패!", Toast.LENGTH_SHORT).show();
        }
    }
}

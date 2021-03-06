package com.denesgarda.JChatClient;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

public class Window extends JFrame {
    private JTextField textField1;
    private JTextArea textArea1;
    private JScrollPane scrollPane1;
    private JPanel panel;

    public Window(Socket socket) {
        super("JChatClient");
        textArea1.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textArea1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        TextAreaOutputStream out = new TextAreaOutputStream(textArea1, "");
        System.setOut(new PrintStream(out));
        this.setSize(1024, 512);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.setVisible(true);
        JFrame frame = this;
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(!textField1.getText().isBlank()) {
                        try {
                            String input = textField1.getText();
                            if(input.equalsIgnoreCase("/exit") || input.equalsIgnoreCase("/quit")) {
                                System.exit(0);
                            }
                            else if(input.equalsIgnoreCase("/leave")) {
                                socket.close();
                                frame.setVisible(false);
                                new Request();
                            }
                            else{
                                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                out.write(input);
                                out.newLine();
                                out.flush();
                            }
                        }
                        catch(SocketException se) {
                            JOptionPane.showMessageDialog(null, "An Error Occurred! Likely cause: Server crashed");
                            System.exit(0);
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    textField1.setText("");
                }
            }
        });
        textArea1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    textField1.requestFocus();
                }
                if(e.getKeyCode() == KeyEvent.VK_SLASH) {
                    textField1.requestFocus();
                    if(textField1.getText().isBlank()) {
                        textField1.setText("/");

                    }
                }
            }
        });
        textField1.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fe) {
                textField1.setCaretPosition(textField1.getDocument().getLength());
            }

            public void focusLost(FocusEvent fe) {
            }
        });
        textField1.requestFocus();
    }
}

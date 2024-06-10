package com.istef.OutdoorActivity2024.service;

import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.model.io.OutputOption;
import com.istef.OutdoorActivity2024.model.io.OutputUserChoice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UIService {

    public static void outputChoiceDialog(Consumer<OutputUserChoice> callback) {
        final JDialog dialog = new JDialog();
        dialog.setTitle("Outdoor Activity 2024");
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.setSize(300, 200);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        JPanel paneButtons = new JPanel(null);
        paneButtons.setSize(300, 220);
        paneButtons.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel info = new JLabel("Select how to be notified:");
        info.setBounds(20, 4, 200, 36);

        JRadioButton radioCalendar = new JRadioButton("Google Calendar", true);
        radioCalendar.setBounds(20, 34, 150, 36);

        JRadioButton radioMail = new JRadioButton("Mail");
        radioMail.setBounds(20, 62, 70, 36);

        JRadioButton radioCli = new JRadioButton("Command line");
        radioCli.setBounds(20, 90, 150, 36);

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(radioCalendar);
        radioGroup.add(radioMail);
        radioGroup.add(radioCli);

        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(80, 64, 200, 30);
        txtEmail.setVisible(false);
        txtEmail.setToolTipText("Enter valid E-mail");

        JButton btnOk = new JButton("OK");
        btnOk.setBounds(126, 130, 50, 36);
        btnOk.addActionListener(e -> {
            OutputOption outputOption = null;
            Map<String, String> selectedOutputProps = new HashMap<>();
            if (radioCalendar.isSelected())
                outputOption = OutputOption.GOOGLE_CALENDAR;
            if (radioCli.isSelected())
                outputOption = OutputOption.CLI;
            if (radioMail.isSelected()) {
                outputOption = OutputOption.MAIL;
                selectedOutputProps.put(outputOption.getPropKeys()[0],
                        txtEmail.getText());
            }

            callback.accept(new OutputUserChoice(outputOption, selectedOutputProps));
            dialog.dispose();
        });

        radioMail.addItemListener(e ->
                txtEmail.setVisible(e.getStateChange() == ItemEvent.SELECTED));

        JPanel paneContent = new JPanel(null);
        paneContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        paneContent.add(info);
        paneContent.add(radioMail);
        paneContent.add(radioCalendar);
        paneContent.add(radioCli);
        paneContent.add(txtEmail);
        paneContent.add(btnOk);
        dialog.setContentPane(paneContent);

        dialog.setVisible(true);
    }

    public static void csvFileDialog() {
        final String MESSAGE = "Please, fill in the 'activities.csv' configuration file.";

        JOptionPane.showMessageDialog(null,
                MESSAGE,
                OutdoorActivityApp.APP_NAME,
                JOptionPane.INFORMATION_MESSAGE);
    }
}

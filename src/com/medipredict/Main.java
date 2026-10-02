package com.medipredict;

import com.medipredict.database.DatabaseInitializer;
import com.medipredict.prediction.ConditionKnowledgeBase;
import com.medipredict.ui.MainFrame;
import com.medipredict.util.Logger;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Logger.info("Starting MediPredict Application...");

        try {
            // 1. Initialize Database & Seed Sample Data
            DatabaseInitializer.initializeDatabase();

            // 2. Load Medical Knowledge Base
            ConditionKnowledgeBase.reload();

            // 3. Launch Graphical Interface
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                Logger.info("MediPredict UI launched successfully.");
            });

        } catch (Exception e) {
            Logger.error("Fatal error during MediPredict application startup", e);
            System.err.println("Fatal startup failure: " + e.getMessage());
        }
    }
}

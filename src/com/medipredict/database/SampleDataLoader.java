package com.medipredict.database;

import com.medipredict.util.Logger;
import com.medipredict.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SampleDataLoader {

    public static void seedSampleData(Connection conn) {
        try {
            Logger.info("Seeding structured medical knowledge base and sample accounts...");

            // 1. Seed Symptoms (symptom_id, name, category, description, severity_weight)
            String[][] symptomsData = {
                    {"Fever", "General", "Elevated body temperature above 38°C (100.4°F)", "2"},
                    {"Headache", "Neurological", "Pain or throbbing sensation in head or upper neck", "2"},
                    {"Cough", "Respiratory", "Dry or productive cough expelling air from lungs", "2"},
                    {"Cold", "Respiratory", "Sneezing, congestion, and runny nose", "1"},
                    {"Sore Throat", "ENT", "Pain, scratchiness, or irritation of the throat", "2"},
                    {"Fatigue", "General", "Extreme tiredness, lethargy, or lack of energy", "1"},
                    {"Nausea", "Digestive", "Feeling of sickness with an inclination to vomit", "2"},
                    {"Vomiting", "Digestive", "Involuntary ejection of stomach contents", "3"},
                    {"Stomach Pain", "Digestive", "Cramping, aching, or sharp abdominal discomfort", "2"},
                    {"Chest Pain", "Cardiovascular", "Discomfort, pressure, or tightness in the chest", "3"},
                    {"Dizziness", "Neurological", "Lightheadedness, unsteadiness, or feeling faint", "2"},
                    {"Shortness of Breath", "Respiratory", "Difficulty breathing or feeling out of breath", "3"},
                    {"Joint Pain", "Musculoskeletal", "Stiffness, aching, or swelling in joints", "2"},
                    {"Muscle Pain", "Musculoskeletal", "Body aches, soreness, and muscle fatigue (myalgia)", "1"},
                    {"Abdominal Pain", "Digestive", "Aching or localized pain in the lower or upper abdomen", "2"},
                    {"Skin Rash", "Dermatological", "Redness, hives, itching, or skin irritation", "1"},
                    {"Loss of Appetite", "Digestive", "Reduced desire to eat or early satiety", "1"},
                    {"Diarrhea", "Digestive", "Frequent loose or watery bowel movements", "2"},
                    {"Back Pain", "Musculoskeletal", "Aching or stiffness in lumbar or upper spine", "1"},
                    {"Body Pain", "General", "Generalized diffuse soreness across the body", "1"},
                    {"Sensitivity to Light", "Neurological", "Photophobia, eye discomfort in bright lighting", "2"},
                    {"Sneezing", "Respiratory", "Sudden involuntary expulsion of air through nose", "1"},
                    {"Runny Nose", "ENT", "Excess nasal drainage or rhinorrhea", "1"},
                    {"Chills", "General", "Feeling cold with shivering or shivering spells", "2"},
                    {"Acid Reflux / Heartburn", "Digestive", "Burning chest or throat pain after eating", "2"},
                    {"Wheezing", "Respiratory", "High-pitched whistling sound while breathing", "3"},
                    {"Congestion", "ENT", "Stuffy sensation in sinuses or nasal passages", "1"}
            };

            String insertSymptom = "INSERT INTO symptoms (name, category, description, severity_weight) VALUES (?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertSymptom)) {
                for (String[] s : symptomsData) {
                    ps.setString(1, s[0]);
                    ps.setString(2, s[1]);
                    ps.setString(3, s[2]);
                    ps.setInt(4, Integer.parseInt(s[3]));
                    ps.executeUpdate();
                }
            }

            // 2. Seed Conditions (name, category, description, precautions, severity_level)
            String[][] conditionsData = {
                    {
                            "Common Cold",
                            "Respiratory",
                            "A viral infection of the upper respiratory tract causing nasal congestion, sneezing, and mild fatigue.",
                            "• Stay well-hydrated with warm liquids.\n• Get 7-8 hours of restful sleep.\n• Use saline nasal sprays or warm steam inhalation.\n• Avoid sharing utensils or towels.",
                            "Mild"
                    },
                    {
                            "Influenza (Flu)",
                            "Respiratory",
                            "A contagious respiratory viral illness characterized by sudden high fever, severe body aches, chills, and fatigue.",
                            "• Strict bed rest for at least 48 to 72 hours.\n• Drink plenty of water, electrolyte drinks, and warm broths.\n• Take doctor-approved antipyretics for fever control.\n• Seek medical attention if breathing becomes difficult.",
                            "Moderate"
                    },
                    {
                            "Migraine",
                            "Neurological",
                            "A neurological condition characterized by intense, throbbing unilateral headache often accompanied by nausea and light sensitivity.",
                            "• Rest in a quiet, dark, and cool room.\n• Apply a cold compress to your forehead or temples.\n• Stay hydrated and avoid caffeine or trigger foods.\n• Consult a neurologist if episodes become frequent.",
                            "Moderate"
                    },
                    {
                            "Gastritis / Acid Reflux",
                            "Digestive",
                            "Inflammation of the stomach lining causing burning upper abdominal discomfort, acid reflux, nausea, and indigestion.",
                            "• Eat smaller, more frequent meals; avoid spicy or fried foods.\n• Do not lie down within 2-3 hours after eating.\n• Avoid alcohol, carbonated drinks, and smoking.\n• Consult a gastroenterologist if pain is persistent.",
                            "Moderate"
                    },
                    {
                            "Food Poisoning",
                            "Digestive",
                            "An illness caused by eating contaminated food or water, leading to nausea, vomiting, stomach cramps, and diarrhea.",
                            "• Replenish fluids with oral rehydration solutions (ORS).\n• Stick to the BRAT diet (Bananas, Rice, Applesauce, Toast).\n• Avoid dairy, caffeine, and heavy greasy foods.\n• Seek immediate medical care if dehydration or high fever occurs.",
                            "Moderate"
                    },
                    {
                            "Viral Fever",
                            "General",
                            "A broad viral illness causing high body temperature, chills, generalized weakness, and muscular soreness.",
                            "• Monitor body temperature every 4-6 hours.\n• Get adequate sleep and avoid physical overexertion.\n• Maintain adequate fluid intake.\n• Consult a physician if fever lasts more than 3 consecutive days.",
                            "Moderate"
                    },
                    {
                            "Sinusitis",
                            "ENT",
                            "Inflammation or swelling of tissue lining the sinuses, causing facial pressure, headache, congestion, and nasal discharge.",
                            "• Inhale steam 2-3 times daily.\n• Use warm compresses over the nose, forehead, and eyes.\n• Drink warm herbal teas to thin mucus secretions.\n• Consult an ENT specialist if facial swelling or fever develops.",
                            "Mild"
                    },
                    {
                            "Bronchitis",
                            "Respiratory",
                            "Inflammation of the lining of bronchial tubes causing persistent cough, chest discomfort, shortness of breath, and fatigue.",
                            "• Use a room humidifier or take steamy showers.\n• Avoid smoke, dust, fumes, and cold dry air.\n• Drink warm honey-lemon water to soothe bronchial passages.\n• Seek doctor consultation if wheezing or shortness of breath worsens.",
                            "Moderate"
                    },
                    {
                            "Gastroenteritis (Stomach Flu)",
                            "Digestive",
                            "Intestinal infection marked by watery diarrhea, abdominal cramps, nausea or vomiting, and sometimes low fever.",
                            "• Sip water, clear broths, or electrolyte solutions frequently.\n• Avoid solid dairy foods until digestive tract stabilizes.\n• Rest your stomach for a few hours after vomiting spells.\n• Consult a doctor if unable to keep liquids down for 24 hours.",
                            "Moderate"
                    },
                    {
                            "Allergic Rhinitis / Allergy",
                            "Immunological",
                            "Allergic response to airborne allergens such as pollen, dust mites, or pet dander causing sneezing, itchy throat, runny nose, and rash.",
                            "• Identify and minimize exposure to known environmental allergens.\n• Keep windows closed during high pollen counts.\n• Use saline nasal washes.\n• Consult an allergist for targeted antihistamine therapies.",
                            "Mild"
                    },
                    {
                            "Tension Headache",
                            "Neurological",
                            "Mild to moderate dull, aching pain often described as a tight band around the head, frequently triggered by stress or poor posture.",
                            "• Practice gentle neck and shoulder stretching exercises.\n• Take regular screen breaks (follow the 20-20-20 rule).\n• Maintain regular sleep and meal schedules.\n• Apply heat packs to tight neck and shoulder muscles.",
                            "Mild"
                    },
                    {
                            "Hypertension / Cardiovascular Strain",
                            "Cardiovascular",
                            "Elevated blood pressure presenting with dizziness, morning headache, chest tightness, or shortness of breath.",
                            "• Reduce sodium and saturated fat intake.\n• Avoid smoking and high caffeine consumption.\n• Practice daily stress management and light walking.\n• URGENT: Consult a cardiologist immediately for accurate blood pressure monitoring.",
                            "Severe"
                    }
            };

            String insertCondition = "INSERT INTO conditions (name, category, description, precautions, severity_level) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(insertCondition)) {
                for (String[] c : conditionsData) {
                    ps.setString(1, c[0]);
                    ps.setString(2, c[1]);
                    ps.setString(3, c[2]);
                    ps.setString(4, c[3]);
                    ps.setString(5, c[4]);
                    ps.executeUpdate();
                }
            }

            // 3. Map Condition Symptoms with weights
            mapConditionSymptoms(conn);

            // 4. Seed Sample Users & Profiles
            seedSampleUsersAndProfiles(conn);

            Logger.info("Sample medical data and accounts successfully populated.");

        } catch (Exception e) {
            Logger.error("Failed to seed sample data", e);
        }
    }

    private static void mapConditionSymptoms(Connection conn) throws Exception {
        // Helper to map by condition name and symptom name
        mapCS(conn, "Common Cold", new String[]{"Cold", "Runny Nose", "Sneezing", "Sore Throat", "Congestion", "Cough"}, new int[]{3, 3, 3, 2, 2, 2});
        mapCS(conn, "Influenza (Flu)", new String[]{"Fever", "Chills", "Body Pain", "Muscle Pain", "Fatigue", "Headache", "Cough"}, new int[]{3, 3, 3, 3, 3, 2, 2});
        mapCS(conn, "Migraine", new String[]{"Headache", "Sensitivity to Light", "Nausea", "Dizziness", "Vomiting"}, new int[]{3, 3, 3, 2, 2});
        mapCS(conn, "Gastritis / Acid Reflux", new String[]{"Stomach Pain", "Acid Reflux / Heartburn", "Nausea", "Abdominal Pain", "Loss of Appetite"}, new int[]{3, 3, 2, 2, 2});
        mapCS(conn, "Food Poisoning", new String[]{"Vomiting", "Diarrhea", "Stomach Pain", "Nausea", "Abdominal Pain", "Fever", "Fatigue"}, new int[]{3, 3, 3, 3, 2, 2, 2});
        mapCS(conn, "Viral Fever", new String[]{"Fever", "Chills", "Body Pain", "Fatigue", "Headache", "Loss of Appetite"}, new int[]{3, 3, 3, 2, 2, 2});
        mapCS(conn, "Sinusitis", new String[]{"Headache", "Congestion", "Runny Nose", "Cold", "Fatigue", "Fever"}, new int[]{3, 3, 3, 2, 1, 1});
        mapCS(conn, "Bronchitis", new String[]{"Cough", "Chest Pain", "Shortness of Breath", "Wheezing", "Fatigue", "Fever"}, new int[]{3, 3, 3, 3, 2, 2});
        mapCS(conn, "Gastroenteritis (Stomach Flu)", new String[]{"Diarrhea", "Vomiting", "Stomach Pain", "Nausea", "Fever", "Fatigue"}, new int[]{3, 3, 3, 2, 2, 2});
        mapCS(conn, "Allergic Rhinitis / Allergy", new String[]{"Sneezing", "Runny Nose", "Cold", "Skin Rash", "Sore Throat", "Congestion"}, new int[]{3, 3, 2, 2, 2, 2});
        mapCS(conn, "Tension Headache", new String[]{"Headache", "Fatigue", "Muscle Pain", "Back Pain"}, new int[]{3, 2, 2, 2});
        mapCS(conn, "Hypertension / Cardiovascular Strain", new String[]{"Chest Pain", "Shortness of Breath", "Dizziness", "Headache", "Fatigue"}, new int[]{3, 3, 3, 2, 2});
    }

    private static void mapCS(Connection conn, String condName, String[] symptoms, int[] weights) throws Exception {
        int condId = -1;
        try (PreparedStatement ps = conn.prepareStatement("SELECT condition_id FROM conditions WHERE name = ?;")) {
            ps.setString(1, condName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    condId = rs.getInt("condition_id");
                }
            }
        }
        if (condId == -1) return;

        for (int i = 0; i < symptoms.length; i++) {
            String symName = symptoms[i];
            int weight = weights[i];
            int symId = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT symptom_id FROM symptoms WHERE name = ?;")) {
                ps.setString(1, symName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        symId = rs.getInt("symptom_id");
                    }
                }
            }
            if (symId != -1) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT OR IGNORE INTO condition_symptoms (condition_id, symptom_id, weight) VALUES (?, ?, ?);")) {
                    ps.setInt(1, condId);
                    ps.setInt(2, symId);
                    ps.setInt(3, weight);
                    ps.executeUpdate();
                } catch (Exception ex) {
                    // In MySQL, fallback without OR IGNORE if needed
                    try (PreparedStatement ps2 = conn.prepareStatement(
                            "INSERT INTO condition_symptoms (condition_id, symptom_id, weight) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE weight=? ;")) {
                        ps2.setInt(1, condId);
                        ps2.setInt(2, symId);
                        ps2.setInt(3, weight);
                        ps2.setInt(4, weight);
                        ps2.executeUpdate();
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    private static void seedSampleUsersAndProfiles(Connection conn) throws Exception {
        // Patient 1: Sarvesh Kumar (patient@medipredict.com / patient123)
        int p1UserId = insertUser(conn, "patient@medipredict.com", "patient123", "PATIENT", "Sarvesh Kumar", "+1 (555) 234-5678");
        int patient1Id = insertPatient(conn, p1UserId, "2002-05-15", "Male", "O+", "No chronic illnesses. Mild seasonal allergies.", "+1 (555) 987-6543 (Father)");

        // Patient 2: Emma Watson (emma@medipredict.com / patient123)
        int p2UserId = insertUser(conn, "emma@medipredict.com", "patient123", "PATIENT", "Emma Watson", "+1 (555) 345-6789");
        int patient2Id = insertPatient(conn, p2UserId, "1998-08-22", "Female", "A+", "Occasional tension headaches.", "+1 (555) 876-5432 (Mother)");

        // Doctor 1: Dr. Emily Watson (doctor@medipredict.com / doctor123)
        int d1UserId = insertUser(conn, "doctor@medipredict.com", "doctor123", "DOCTOR", "Dr. Emily Watson, MD", "+1 (555) 888-1122");
        int doctor1Id = insertDoctor(conn, d1UserId, "General Physician", "City Care Memorial Hospital", 12, 60.00,
                "Board-certified physician with 12+ years treating general, acute, and chronic medical illnesses.", "8:30 AM - 4:30 PM");

        // Doctor 2: Dr. Robert Chen (chen@medipredict.com / doctor123)
        int d2UserId = insertUser(conn, "chen@medipredict.com", "doctor123", "DOCTOR", "Dr. Robert Chen, MD", "+1 (555) 888-3344");
        int doctor2Id = insertDoctor(conn, d2UserId, "Neurologist", "Metro Neurology & Brain Center", 15, 95.00,
                "Senior Neurologist specializing in migraine management, tension headaches, and nervous system disorders.", "9:00 AM - 5:00 PM");

        // Doctor 3: Dr. Priya Sharma (sharma@medipredict.com / doctor123)
        int d3UserId = insertUser(conn, "sharma@medipredict.com", "doctor123", "DOCTOR", "Dr. Priya Sharma, MD", "+1 (555) 888-5566");
        int doctor3Id = insertDoctor(conn, d3UserId, "Pulmonologist", "Apollo Health & Chest Institute", 10, 75.00,
                "Chest and respiratory specialist focusing on asthma, bronchitis, viral infections, and respiratory allergies.", "10:00 AM - 6:00 PM");

        // Doctor 4: Dr. James Wilson (wilson@medipredict.com / doctor123)
        int d4UserId = insertUser(conn, "wilson@medipredict.com", "doctor123", "DOCTOR", "Dr. James Wilson, MD", "+1 (555) 888-7788");
        int doctor4Id = insertDoctor(conn, d4UserId, "Gastroenterologist", "St. Jude Digestive Care", 14, 85.00,
                "Expert in digestive system disorders, gastritis, acid reflux, and food poisoning recovery.", "9:00 AM - 4:00 PM");

        // Doctor 5: Dr. Linda Martinez (martinez@medipredict.com / doctor123)
        int d5UserId = insertUser(conn, "martinez@medipredict.com", "doctor123", "DOCTOR", "Dr. Linda Martinez, MD", "+1 (555) 888-9900");
        int doctor5Id = insertDoctor(conn, d5UserId, "Cardiologist", "Heart & Vascular Pavilion", 18, 110.00,
                "Cardiovascular consultant managing hypertension, coronary health, and chest discomfort evaluation.", "8:00 AM - 2:00 PM");

        // Seed Sample Prediction for Patient 1 (Migraine)
        int predId = insertPrediction(conn, patient1Id, 3, 88.5, "High", "Moderate", "Patient reported headache with nausea and light sensitivity.");
        insertPredictionSymptom(conn, predId, 2); // Headache
        insertPredictionSymptom(conn, predId, 7); // Nausea
        insertPredictionSymptom(conn, predId, 21); // Sensitivity to Light

        // Seed Sample Appointment for Patient 1 with Doctor 2 (Dr. Robert Chen)
        insertAppointment(conn, patient1Id, doctor2Id, "2026-09-25", "10:30 AM", "Follow-up consultation for recurring migraine headaches and light sensitivity.", "CONFIRMED");

        // Seed Sample Appointment for Patient 1 with Doctor 1 (Dr. Emily Watson)
        insertAppointment(conn, patient1Id, doctor1Id, "2026-09-28", "02:00 PM", "Routine general health assessment and blood pressure check.", "PENDING");

        // Seed Sample Recommendation from Doctor 2 to Patient 1
        insertRecommendation(conn, patient1Id, doctor2Id, predId,
                "Patient exhibits symptoms consistent with acute migraine episodes. Recommended keeping a headache diary to log dietary triggers and sleep patterns.",
                "Maintain adequate hydration (minimum 2.5L/day), limit blue light exposure after 9 PM, and rest in a dark, quiet environment during flare-ups.",
                "2026-10-05", "Schedule follow-up if episodes exceed twice weekly.");

        // Seed Sample Messages between Patient 1 and Doctor 2
        insertMessage(conn, p1UserId, d2UserId, "Hello Dr. Chen, I've been experiencing sensitivity to light along with throbbing headaches for the past 2 days.", 1);
        insertMessage(conn, d2UserId, p1UserId, "Hello Sarvesh, I have reviewed your symptom report. Please rest in a dimly lit room and stay well hydrated. We will evaluate further during our upcoming appointment on Sep 25th.", 1);
        insertMessage(conn, p1UserId, d2UserId, "Thank you Doctor, looking forward to the consultation.", 0);
    }

    private static int insertUser(Connection conn, String email, String password, String role, String fullName, String phone) throws Exception {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        String sql = "INSERT INTO users (email, password_hash, salt, role, full_name, phone) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.setString(4, role);
            ps.setString(5, fullName);
            ps.setString(6, phone);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private static int insertPatient(Connection conn, int userId, String dob, String gender, String bloodGroup, String history, String emergency) throws Exception {
        String sql = "INSERT INTO patients (user_id, dob, gender, blood_group, medical_history, emergency_contact) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, dob);
            ps.setString(3, gender);
            ps.setString(4, bloodGroup);
            ps.setString(5, history);
            ps.setString(6, emergency);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private static int insertDoctor(Connection conn, int userId, String spec, String hospital, int exp, double fee, String bio, String hours) throws Exception {
        String sql = "INSERT INTO doctors (user_id, specialization, hospital_clinic, experience_years, consultation_fee, bio, availability_hours) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, spec);
            ps.setString(3, hospital);
            ps.setInt(4, exp);
            ps.setDouble(5, fee);
            ps.setString(6, bio);
            ps.setString(7, hours);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private static int insertPrediction(Connection conn, int patientId, int condId, double score, String conf, String risk, String notes) throws Exception {
        String sql = "INSERT INTO predictions (patient_id, primary_condition_id, match_score, confidence_level, risk_level, notes) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId);
            ps.setInt(2, condId);
            ps.setDouble(3, score);
            ps.setString(4, conf);
            ps.setString(5, risk);
            ps.setString(6, notes);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private static void insertPredictionSymptom(Connection conn, int predId, int symId) throws Exception {
        String sql = "INSERT INTO prediction_symptoms (prediction_id, symptom_id) VALUES (?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, predId);
            ps.setInt(2, symId);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private static void insertAppointment(Connection conn, int patientId, int doctorId, String date, String time, String reason, String status) throws Exception {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, reason);
            ps.setString(6, status);
            ps.executeUpdate();
        }
    }

    private static void insertRecommendation(Connection conn, int patientId, int doctorId, int predId, String text, String lifestyle, String followUp, String notes) throws Exception {
        String sql = "INSERT INTO recommendations (patient_id, doctor_id, prediction_id, recommendation_text, lifestyle_advice, follow_up_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setInt(3, predId);
            ps.setString(4, text);
            ps.setString(5, lifestyle);
            ps.setString(6, followUp);
            ps.setString(7, notes);
            ps.executeUpdate();
        }
    }

    private static void insertMessage(Connection conn, int senderId, int receiverId, String text, int isRead) throws Exception {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_text, is_read) VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, text);
            ps.setInt(4, isRead);
            ps.executeUpdate();
        }
    }
}

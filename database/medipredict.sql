-- =====================================================================
-- MediPredict: Smart Medical Condition Prediction & Consultation System
-- MySQL Database Setup & Seed Script
-- =====================================================================

CREATE DATABASE IF NOT EXISTS medipredict CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medipredict;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(25),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    dob DATE,
    gender VARCHAR(20),
    blood_group VARCHAR(10),
    medical_history TEXT,
    emergency_contact VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL,
    hospital_clinic VARCHAR(150) NOT NULL,
    experience_years INT DEFAULT 0,
    consultation_fee DECIMAL(10, 2) DEFAULT 50.00,
    bio TEXT,
    availability_hours VARCHAR(100) DEFAULT '9:00 AM - 5:00 PM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. Symptoms Table
CREATE TABLE IF NOT EXISTS symptoms (
    symptom_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    severity_weight INT DEFAULT 1
);

-- 5. Conditions Table
CREATE TABLE IF NOT EXISTS conditions (
    condition_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    precautions TEXT NOT NULL,
    severity_level VARCHAR(20) DEFAULT 'Moderate'
);

-- 6. Condition_Symptoms Table
CREATE TABLE IF NOT EXISTS condition_symptoms (
    condition_id INT NOT NULL,
    symptom_id INT NOT NULL,
    weight INT DEFAULT 1,
    PRIMARY KEY (condition_id, symptom_id),
    FOREIGN KEY (condition_id) REFERENCES conditions(condition_id) ON DELETE CASCADE,
    FOREIGN KEY (symptom_id) REFERENCES symptoms(symptom_id) ON DELETE CASCADE
);

-- 7. Predictions Table
CREATE TABLE IF NOT EXISTS predictions (
    prediction_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    primary_condition_id INT NOT NULL,
    match_score DOUBLE DEFAULT 0.0,
    confidence_level VARCHAR(20) DEFAULT 'Medium',
    risk_level VARCHAR(30) DEFAULT 'Moderate',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (primary_condition_id) REFERENCES conditions(condition_id) ON DELETE CASCADE
);

-- 8. Prediction_Symptoms Table
CREATE TABLE IF NOT EXISTS prediction_symptoms (
    prediction_id INT NOT NULL,
    symptom_id INT NOT NULL,
    PRIMARY KEY (prediction_id, symptom_id),
    FOREIGN KEY (prediction_id) REFERENCES predictions(prediction_id) ON DELETE CASCADE,
    FOREIGN KEY (symptom_id) REFERENCES symptoms(symptom_id) ON DELETE CASCADE
);

-- 9. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time VARCHAR(20) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- 10. Recommendations Table
CREATE TABLE IF NOT EXISTS recommendations (
    recommendation_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prediction_id INT,
    recommendation_text TEXT NOT NULL,
    lifestyle_advice TEXT,
    follow_up_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    FOREIGN KEY (prediction_id) REFERENCES predictions(prediction_id) ON DELETE SET NULL
);

-- 11. Messages Table
CREATE TABLE IF NOT EXISTS messages (
    message_id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_text TEXT NOT NULL,
    is_read INT DEFAULT 0,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =====================================================================
-- Sample Data Insertion
-- =====================================================================

INSERT IGNORE INTO symptoms (name, category, description, severity_weight) VALUES
('Fever', 'General', 'Elevated body temperature above 38°C (100.4°F)', 2),
('Headache', 'Neurological', 'Pain or throbbing sensation in head or upper neck', 2),
('Cough', 'Respiratory', 'Dry or productive cough expelling air from lungs', 2),
('Cold', 'Respiratory', 'Sneezing, congestion, and runny nose', 1),
('Sore Throat', 'ENT', 'Pain, scratchiness, or irritation of the throat', 2),
('Fatigue', 'General', 'Extreme tiredness, lethargy, or lack of energy', 1),
('Nausea', 'Digestive', 'Feeling of sickness with an inclination to vomit', 2),
('Vomiting', 'Digestive', 'Involuntary ejection of stomach contents', 3),
('Stomach Pain', 'Digestive', 'Cramping, aching, or sharp abdominal discomfort', 2),
('Chest Pain', 'Cardiovascular', 'Discomfort, pressure, or tightness in the chest', 3),
('Dizziness', 'Neurological', 'Lightheadedness, unsteadiness, or feeling faint', 2),
('Shortness of Breath', 'Respiratory', 'Difficulty breathing or feeling out of breath', 3),
('Joint Pain', 'Musculoskeletal', 'Stiffness, aching, or swelling in joints', 2),
('Muscle Pain', 'Musculoskeletal', 'Body aches, soreness, and muscle fatigue', 1),
('Abdominal Pain', 'Digestive', 'Aching or localized pain in the abdomen', 2),
('Skin Rash', 'Dermatological', 'Redness, hives, itching, or skin irritation', 1),
('Loss of Appetite', 'Digestive', 'Reduced desire to eat or early satiety', 1),
('Diarrhea', 'Digestive', 'Frequent loose or watery bowel movements', 2),
('Back Pain', 'Musculoskeletal', 'Aching or stiffness in lumbar or upper spine', 1),
('Body Pain', 'General', 'Generalized diffuse soreness across the body', 1),
('Sensitivity to Light', 'Neurological', 'Photophobia, eye discomfort in bright lighting', 2),
('Sneezing', 'Respiratory', 'Sudden involuntary expulsion of air through nose', 1),
('Runny Nose', 'ENT', 'Excess nasal drainage or rhinorrhea', 1),
('Chills', 'General', 'Feeling cold with shivering or shivering spells', 2),
('Acid Reflux / Heartburn', 'Digestive', 'Burning chest or throat pain after eating', 2),
('Wheezing', 'Respiratory', 'High-pitched whistling sound while breathing', 3),
('Congestion', 'ENT', 'Stuffy sensation in sinuses or nasal passages', 1);

INSERT IGNORE INTO conditions (name, category, description, precautions, severity_level) VALUES
('Common Cold', 'Respiratory', 'A viral infection of the upper respiratory tract causing nasal congestion, sneezing, and mild fatigue.', '• Stay well-hydrated with warm liquids.\n• Get 7-8 hours of restful sleep.\n• Use saline nasal sprays or warm steam inhalation.', 'Mild'),
('Influenza (Flu)', 'Respiratory', 'A contagious respiratory viral illness characterized by sudden high fever, severe body aches, chills, and fatigue.', '• Strict bed rest for at least 48 to 72 hours.\n• Drink plenty of water and warm broths.\n• Consult a physician if breathing becomes difficult.', 'Moderate'),
('Migraine', 'Neurological', 'A neurological condition characterized by intense, throbbing unilateral headache often accompanied by nausea and light sensitivity.', '• Rest in a quiet, dark, and cool room.\n• Apply a cold compress to forehead or temples.\n• Stay hydrated and avoid caffeine triggers.', 'Moderate'),
('Gastritis / Acid Reflux', 'Digestive', 'Inflammation of the stomach lining causing burning upper abdominal discomfort, acid reflux, nausea, and indigestion.', '• Eat smaller, frequent meals; avoid spicy foods.\n• Do not lie down within 2-3 hours after eating.\n• Avoid alcohol and carbonated drinks.', 'Moderate'),
('Food Poisoning', 'Digestive', 'An illness caused by eating contaminated food or water, leading to nausea, vomiting, stomach cramps, and diarrhea.', '• Replenish fluids with oral rehydration solutions (ORS).\n• Stick to the BRAT diet (Bananas, Rice, Applesauce, Toast).\n• Seek immediate medical care if dehydration occurs.', 'Moderate'),
('Viral Fever', 'General', 'A broad viral illness causing high body temperature, chills, generalized weakness, and muscular soreness.', '• Monitor body temperature every 4-6 hours.\n• Get adequate sleep and avoid physical overexertion.\n• Consult a physician if fever lasts more than 3 days.', 'Moderate'),
('Sinusitis', 'ENT', 'Inflammation or swelling of tissue lining the sinuses, causing facial pressure, headache, congestion, and nasal discharge.', '• Inhale steam 2-3 times daily.\n• Use warm compresses over the nose and eyes.\n• Drink warm herbal teas to thin mucus.', 'Mild'),
('Bronchitis', 'Respiratory', 'Inflammation of the lining of bronchial tubes causing persistent cough, chest discomfort, shortness of breath, and fatigue.', '• Use a room humidifier or take steamy showers.\n• Avoid smoke, dust, and cold dry air.\n• Seek doctor consultation if wheezing worsens.', 'Moderate'),
('Gastroenteritis (Stomach Flu)', 'Digestive', 'Intestinal infection marked by watery diarrhea, abdominal cramps, nausea or vomiting, and sometimes low fever.', '• Sip water or electrolyte solutions frequently.\n• Avoid solid dairy foods until digestive tract stabilizes.', 'Moderate'),
('Allergic Rhinitis / Allergy', 'Immunological', 'Allergic response to airborne allergens such as pollen, dust mites, or pet dander causing sneezing, itchy throat, runny nose, and rash.', '• Identify and minimize exposure to known environmental allergens.\n• Keep windows closed during high pollen counts.', 'Mild'),
('Tension Headache', 'Neurological', 'Mild to moderate dull, aching pain often described as a tight band around the head, frequently triggered by stress or poor posture.', '• Practice gentle neck and shoulder stretching exercises.\n• Take regular screen breaks (follow 20-20-20 rule).', 'Mild'),
('Hypertension / Cardiovascular Strain', 'Cardiovascular', 'Elevated blood pressure presenting with dizziness, morning headache, chest tightness, or shortness of breath.', '• Reduce sodium and saturated fat intake.\n• Avoid smoking and high caffeine.\n• URGENT: Consult a cardiologist immediately.', 'Severe');

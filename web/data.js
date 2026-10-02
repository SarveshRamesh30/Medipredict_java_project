/**
 * MediPredict Database & Data Layer (South Indian Chennai Healthcare Edition)
 * Persists dynamically to LocalStorage.
 */

const INITIAL_DATA = {
  symptoms: [
    { id: 1, name: "Fever", category: "General", description: "Elevated body temperature above 38°C (100.4°F)", severityWeight: 2 },
    { id: 2, name: "Headache", category: "Neurological", description: "Pain or throbbing sensation in head or upper neck", severityWeight: 2 },
    { id: 3, name: "Cough", category: "Respiratory", description: "Dry or productive cough expelling air from lungs", severityWeight: 2 },
    { id: 4, name: "Cold", category: "Respiratory", description: "Sneezing, congestion, and runny nose", severityWeight: 1 },
    { id: 5, name: "Sore Throat", category: "ENT", description: "Pain, scratchiness, or irritation of the throat", severityWeight: 2 },
    { id: 6, name: "Fatigue", category: "General", description: "Extreme tiredness, lethargy, or lack of energy", severityWeight: 1 },
    { id: 7, name: "Nausea", category: "Digestive", description: "Feeling of sickness with an inclination to vomit", severityWeight: 2 },
    { id: 8, name: "Vomiting", category: "Digestive", description: "Involuntary ejection of stomach contents", severityWeight: 3 },
    { id: 9, name: "Stomach Pain", category: "Digestive", description: "Cramping, aching, or sharp abdominal discomfort", severityWeight: 2 },
    { id: 10, name: "Chest Pain", category: "Cardiovascular", description: "Discomfort, pressure, or tightness in the chest", severityWeight: 3 },
    { id: 11, name: "Dizziness", category: "Neurological", description: "Lightheadedness, unsteadiness, or feeling faint", severityWeight: 2 },
    { id: 12, name: "Shortness of Breath", category: "Respiratory", description: "Difficulty breathing or feeling out of breath", severityWeight: 3 },
    { id: 13, name: "Joint Pain", category: "Musculoskeletal", description: "Stiffness, aching, or swelling in joints", severityWeight: 2 },
    { id: 14, name: "Muscle Pain", category: "Musculoskeletal", description: "Body aches, soreness, and muscle fatigue (myalgia)", severityWeight: 1 },
    { id: 15, name: "Abdominal Pain", category: "Digestive", description: "Aching or localized pain in the lower or upper abdomen", severityWeight: 2 },
    { id: 16, name: "Skin Rash", category: "Dermatological", description: "Redness, hives, itching, or skin irritation", severityWeight: 1 },
    { id: 17, name: "Loss of Appetite", category: "Digestive", description: "Reduced desire to eat or early satiety", severityWeight: 1 },
    { id: 18, name: "Diarrhea", category: "Digestive", description: "Frequent loose or watery bowel movements", severityWeight: 2 },
    { id: 19, name: "Back Pain", category: "Musculoskeletal", description: "Aching or stiffness in lumbar or upper spine", severityWeight: 1 },
    { id: 20, name: "Body Pain", category: "General", description: "Generalized diffuse soreness across the body", severityWeight: 1 },
    { id: 21, name: "Sensitivity to Light", category: "Neurological", description: "Photophobia, eye discomfort in bright lighting", severityWeight: 2 },
    { id: 22, name: "Sneezing", category: "Respiratory", description: "Sudden involuntary expulsion of air through nose", severityWeight: 1 },
    { id: 23, name: "Runny Nose", category: "ENT", description: "Excess nasal drainage or rhinorrhea", severityWeight: 1 },
    { id: 24, name: "Chills", category: "General", description: "Feeling cold with shivering or shivering spells", severityWeight: 2 },
    { id: 25, name: "Acid Reflux / Heartburn", category: "Digestive", description: "Burning chest or throat pain after eating", severityWeight: 2 },
    { id: 26, name: "Wheezing", category: "Respiratory", description: "High-pitched whistling sound while breathing", severityWeight: 3 },
    { id: 27, name: "Congestion", category: "ENT", description: "Stuffy sensation in sinuses or nasal passages", severityWeight: 1 }
  ],

  conditions: [
    {
      id: 1,
      name: "Common Cold",
      category: "Respiratory",
      description: "A viral infection of the upper respiratory tract causing nasal congestion, sneezing, and mild fatigue.",
      precautions: "• Stay well-hydrated with warm liquids.\n• Get 7-8 hours of restful sleep.\n• Use saline nasal sprays or warm steam inhalation.\n• Avoid sharing utensils or towels.",
      severityLevel: "Mild",
      symptomIds: [4, 23, 22, 5, 27, 3],
      symptomWeights: [3, 3, 3, 2, 2, 2]
    },
    {
      id: 2,
      name: "Influenza (Flu)",
      category: "Respiratory",
      description: "A contagious respiratory viral illness characterized by sudden high fever, severe body aches, chills, and fatigue.",
      precautions: "• Strict bed rest for at least 48 to 72 hours.\n• Drink plenty of water, tender coconut water, and warm broths.\n• Take doctor-approved antipyretics for fever control.\n• Seek medical attention if breathing becomes difficult.",
      severityLevel: "Moderate",
      symptomIds: [1, 24, 20, 14, 6, 2, 3],
      symptomWeights: [3, 3, 3, 3, 3, 2, 2]
    },
    {
      id: 3,
      name: "Migraine",
      category: "Neurological",
      description: "A neurological condition characterized by intense, throbbing unilateral headache often accompanied by nausea and light sensitivity.",
      precautions: "• Rest in a quiet, dark, and cool room.\n• Apply a cold compress to your forehead or temples.\n• Stay hydrated and avoid caffeine or trigger foods.\n• Consult a neurologist if episodes become frequent.",
      severityLevel: "Moderate",
      symptomIds: [2, 21, 7, 11, 8],
      symptomWeights: [3, 3, 3, 2, 2]
    },
    {
      id: 4,
      name: "Gastritis / Acid Reflux",
      category: "Digestive",
      description: "Inflammation of the stomach lining causing burning upper abdominal discomfort, acid reflux, nausea, and indigestion.",
      precautions: "• Eat smaller, more frequent meals; avoid spicy or fried foods.\n• Do not lie down within 2-3 hours after eating.\n• Avoid alcohol, carbonated drinks, and smoking.\n• Consult a gastroenterologist if pain is persistent.",
      severityLevel: "Moderate",
      symptomIds: [9, 25, 7, 15, 17],
      symptomWeights: [3, 3, 2, 2, 2]
    },
    {
      id: 5,
      name: "Food Poisoning",
      category: "Digestive",
      description: "An illness caused by eating contaminated food or water, leading to nausea, vomiting, stomach cramps, and diarrhea.",
      precautions: "• Replenish fluids with oral rehydration solutions (ORS) and tender coconut water.\n• Stick to easy-to-digest curd rice, idli, or toast.\n• Avoid spicy gravies, dairy products, and oily foods.\n• Seek immediate medical care if severe dehydration or high fever occurs.",
      severityLevel: "Moderate",
      symptomIds: [8, 18, 9, 7, 15, 1, 6],
      symptomWeights: [3, 3, 3, 3, 2, 2, 2]
    },
    {
      id: 6,
      name: "Viral Fever",
      category: "General",
      description: "A broad viral illness causing high body temperature, chills, generalized weakness, and muscular soreness.",
      precautions: "• Monitor body temperature every 4-6 hours.\n• Get adequate rest and drink warm water and rasam/soups.\n• Consult a physician if fever lasts more than 3 consecutive days.",
      severityLevel: "Moderate",
      symptomIds: [1, 24, 20, 6, 2, 17],
      symptomWeights: [3, 3, 3, 2, 2, 2]
    },
    {
      id: 7,
      name: "Sinusitis",
      category: "ENT",
      description: "Inflammation or swelling of tissue lining the sinuses, causing facial pressure, headache, congestion, and nasal discharge.",
      precautions: "• Inhale steam 2-3 times daily.\n• Use warm compresses over the nose, forehead, and eyes.\n• Drink warm herbal water/tea to thin mucus secretions.\n• Consult an ENT specialist if facial swelling or fever develops.",
      severityLevel: "Mild",
      symptomIds: [2, 27, 23, 4, 6, 1],
      symptomWeights: [3, 3, 3, 2, 1, 1]
    },
    {
      id: 8,
      name: "Bronchitis",
      category: "Respiratory",
      description: "Inflammation of the lining of bronchial tubes causing persistent cough, chest discomfort, shortness of breath, and fatigue.",
      precautions: "• Use steam inhalation and avoid air pollutants/dust.\n• Drink warm water with honey and pepper to soothe bronchial passages.\n• Seek doctor consultation if wheezing or breathlessness worsens.",
      severityLevel: "Moderate",
      symptomIds: [3, 10, 12, 26, 6, 1],
      symptomWeights: [3, 3, 3, 3, 2, 2]
    },
    {
      id: 9,
      name: "Gastroenteritis (Stomach Flu)",
      category: "Digestive",
      description: "Intestinal infection marked by watery diarrhea, abdominal cramps, nausea or vomiting, and sometimes low fever.",
      precautions: "• Sip buttermilk with jeera, ORS, and warm water frequently.\n• Avoid solid dairy foods until digestion normalizes.\n• Rest your stomach for a few hours after vomiting spells.\n• Consult a doctor if unable to retain fluids for 24 hours.",
      severityLevel: "Moderate",
      symptomIds: [18, 8, 9, 7, 1, 6],
      symptomWeights: [3, 3, 3, 2, 2, 2]
    },
    {
      id: 10,
      name: "Allergic Rhinitis / Allergy",
      category: "Immunological",
      description: "Allergic response to airborne allergens such as pollen, dust, or pet dander causing sneezing, itchy throat, runny nose, and rash.",
      precautions: "• Identify and minimize exposure to dust and known allergens.\n• Keep windows closed during high dust/pollen periods.\n• Use saline nasal washes.\n• Consult an allergist for targeted antihistamine therapies.",
      severityLevel: "Mild",
      symptomIds: [22, 23, 4, 16, 5, 27],
      symptomWeights: [3, 3, 2, 2, 2, 2]
    },
    {
      id: 11,
      name: "Tension Headache",
      category: "Neurological",
      description: "Mild to moderate dull, aching pain often described as a tight band around the head, frequently triggered by stress or poor posture.",
      precautions: "• Practice gentle neck and shoulder stretching exercises.\n• Take regular screen breaks (follow the 20-20-20 rule).\n• Maintain regular sleep and meal schedules.\n• Apply warm packs to tight neck muscles.",
      severityLevel: "Mild",
      symptomIds: [2, 6, 14, 19],
      symptomWeights: [3, 2, 2, 2]
    },
    {
      id: 12,
      name: "Hypertension / Cardiovascular Strain",
      category: "Cardiovascular",
      description: "Elevated blood pressure presenting with dizziness, morning headache, chest tightness, or shortness of breath.",
      precautions: "• Reduce salt (sodium) and oily food intake.\n• Avoid tobacco, smoking, and high caffeine consumption.\n• Practice daily light walking and stress relief.\n• URGENT: Consult a cardiologist immediately for accurate blood pressure monitoring.",
      severityLevel: "Severe",
      symptomIds: [10, 12, 11, 2, 6],
      symptomWeights: [3, 3, 3, 2, 2]
    }
  ],

  users: [
    // Patients
    {
      id: 1,
      email: "sarveshrameshkr@gmail.com",
      role: "PATIENT",
      fullName: "sarvesh",
      phone: "+91 99403 85123",
      patientProfile: {
        id: 1,
        dob: "2002-05-15",
        gender: "Male",
        bloodGroup: "O+",
        medicalHistory: "No chronic illnesses. Mild seasonal allergies.",
        emergencyContact: "Father: +91 98401 23456"
      }
    },
    {
      id: 2,
      email: "ananya.subramanian@gmail.com",
      role: "PATIENT",
      fullName: "Ananya Subramanian",
      phone: "+91 98410 76543",
      patientProfile: {
        id: 2,
        dob: "1999-08-22",
        gender: "Female",
        bloodGroup: "A+",
        medicalHistory: "Occasional tension headaches.",
        emergencyContact: "Mother: +91 98410 11223"
      }
    },

    // Chennai & South Indian Doctors & Specialists
    {
      id: 3,
      email: "dr.radhika@kauveryhospital.com",
      role: "DOCTOR",
      fullName: "Dr. Radhika Sundaram, MD",
      phone: "+91 98400 11223",
      doctorProfile: {
        id: 1,
        specialization: "General Physician",
        hospital: "Kauvery Hospital, Alwarpet, Chennai",
        experienceYears: 12,
        consultationFee: 500,
        bio: "Senior Consultant Physician with 12+ years treating seasonal viral fevers, diabetes, hypertension, and acute medical illnesses in Chennai.",
        availabilityHours: "8:30 AM - 4:30 PM"
      }
    },
    {
      id: 4,
      email: "dr.senthil@apollohospitals.com",
      role: "DOCTOR",
      fullName: "Dr. K. Senthil Nathan, MD, DM",
      phone: "+91 98400 33445",
      doctorProfile: {
        id: 2,
        specialization: "Neurologist",
        hospital: "Apollo Hospitals, Greams Road, Chennai",
        experienceYears: 15,
        consultationFee: 800,
        bio: "Senior Consultant Neurologist specializing in migraine therapy, chronic tension headaches, neuropathy, and nervous system disorders.",
        availabilityHours: "9:00 AM - 5:00 PM"
      }
    },
    {
      id: 5,
      email: "dr.balasubramanian@miot.com",
      role: "DOCTOR",
      fullName: "Dr. V. Balasubramanian, MD, DM",
      phone: "+91 98400 55667",
      doctorProfile: {
        id: 3,
        specialization: "Pulmonologist",
        hospital: "MIOT International, Manapakkam, Chennai",
        experienceYears: 14,
        consultationFee: 750,
        bio: "Chest & Interventional Pulmonology specialist focusing on chronic cough, asthma, bronchitis, respiratory allergies, and viral infections.",
        availabilityHours: "10:00 AM - 6:00 PM"
      }
    },
    {
      id: 6,
      email: "dr.anitha@gleneagles.com",
      role: "DOCTOR",
      fullName: "Dr. Anitha Raghavan, MD, DNB",
      phone: "+91 98400 77889",
      doctorProfile: {
        id: 4,
        specialization: "Gastroenterologist",
        hospital: "Gleneagles Global Health City, Perumbakkam, Chennai",
        experienceYears: 11,
        consultationFee: 700,
        bio: "Expert Gastroenterologist & Hepatologist treating gastritis, acid reflux (GERD), food poisoning, liver health, and digestive ailments.",
        availabilityHours: "9:00 AM - 4:00 PM"
      }
    },
    {
      id: 7,
      email: "dr.venkatesh@mgmhealthcare.com",
      role: "DOCTOR",
      fullName: "Dr. M. S. Venkatesh, MD, DM, FACC",
      phone: "+91 98400 99001",
      doctorProfile: {
        id: 5,
        specialization: "Cardiologist",
        hospital: "MGM Healthcare, Nelson Manickam Road, Chennai",
        experienceYears: 18,
        consultationFee: 900,
        bio: "Senior Interventional Cardiologist managing hypertension, chest pain assessment, cardiac rhythm, and preventative cardiology.",
        availabilityHours: "8:00 AM - 2:00 PM"
      }
    }
  ],

  predictions: [
    {
      id: 1,
      patientId: 1,
      patientName: "sarvesh",
      predictedConditionId: 7,
      predictedConditionName: "Sinusitis",
      matchScore: 88.5,
      confidenceLevel: "High",
      riskLevel: "Moderate",
      notes: "Reported symptoms: Headache, Congestion, Runny Nose, Cold",
      symptomsReported: ["Abdominal Pain", "Body Pain", "Cold", "Dizziness", "Fever", "Headache", "Runny Nose", "Vomiting"],
      createdAt: "2026-09-19T06:54:00.000Z"
    }
  ],

  appointments: [
    {
      id: 1,
      patientId: 1,
      patientName: "sarvesh",
      doctorId: 1,
      doctorName: "Dr. Radhika Sundaram, MD",
      doctorSpec: "General Physician",
      appointmentDate: "2026-10-05",
      appointmentTime: "10:30 AM",
      notes: "General consultation for recurring fever and cold symptoms.",
      status: "CONFIRMED",
      createdAt: "2026-09-19T07:00:00.000Z"
    }
  ],

  recommendations: [],

  messages: [
    {
      id: 1,
      senderId: 1,
      receiverId: 3, // Dr. Radhika Sundaram
      senderName: "sarvesh",
      receiverName: "Dr. Radhika Sundaram, MD",
      content: "hello doctor i have severe fever",
      isRead: 1,
      timestamp: "2026-09-19T06:55:00.000Z"
    }
  ]
};

// Storage Manager
const DB = {
  STORAGE_KEY: "medipredict_chennai_db_v2",

  get() {
    try {
      const data = localStorage.getItem(this.STORAGE_KEY);
      if (data) {
        return JSON.parse(data);
      }
    } catch (e) {
      console.warn("Error reading localStorage, using initial dataset", e);
    }
    this.save(INITIAL_DATA);
    return JSON.parse(JSON.stringify(INITIAL_DATA));
  },

  save(data) {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(data));
    } catch (e) {
      console.error("Error saving data to localStorage", e);
    }
  },

  reset() {
    localStorage.removeItem(this.STORAGE_KEY);
    return this.get();
  },

  getUsers() { return this.get().users; },
  findUserByEmail(email) {
    if (!email) return null;
    return this.getUsers().find(u => u.email.toLowerCase() === email.toLowerCase());
  },
  findUserById(id) { return this.getUsers().find(u => u.id === parseInt(id)); },
  addUser(userObj) {
    const data = this.get();
    userObj.id = (data.users.length ? Math.max(...data.users.map(u => u.id)) : 0) + 1;
    data.users.push(userObj);
    this.save(data);
    return userObj;
  },
  updateUser(userObj) {
    const data = this.get();
    const idx = data.users.findIndex(u => u.id === userObj.id);
    if (idx !== -1) {
      data.users[idx] = userObj;
      this.save(data);
    }
  },

  getDoctors() { return this.get().users.filter(u => u.role === "DOCTOR"); },
  getDoctorByDoctorId(docId) {
    return this.getDoctors().find(d => d.doctorProfile && d.doctorProfile.id === parseInt(docId));
  },

  getPatients() { return this.get().users.filter(u => u.role === "PATIENT"); },
  getPatientByPatientId(patId) {
    return this.getPatients().find(p => p.patientProfile && p.patientProfile.id === parseInt(patId));
  },

  getSymptoms() { return this.get().symptoms; },
  getConditions() { return this.get().conditions; },
  getPredictions() { return this.get().predictions; },
  addPrediction(predObj) {
    const data = this.get();
    predObj.id = (data.predictions.length ? Math.max(...data.predictions.map(p => p.id)) : 0) + 1;
    predObj.createdAt = new Date().toISOString();
    data.predictions.unshift(predObj);
    this.save(data);
    return predObj;
  },

  getAppointments() { return this.get().appointments; },
  addAppointment(apptObj) {
    const data = this.get();
    apptObj.id = (data.appointments.length ? Math.max(...data.appointments.map(a => a.id)) : 0) + 1;
    apptObj.createdAt = new Date().toISOString();
    data.appointments.unshift(apptObj);
    this.save(data);
    return apptObj;
  },
  updateAppointmentStatus(id, newStatus) {
    const data = this.get();
    const appt = data.appointments.find(a => a.id === parseInt(id));
    if (appt) {
      appt.status = newStatus;
      this.save(data);
    }
  },

  getRecommendations() { return this.get().recommendations; },
  addRecommendation(recObj) {
    const data = this.get();
    recObj.id = (data.recommendations.length ? Math.max(...data.recommendations.map(r => r.id)) : 0) + 1;
    recObj.createdAt = new Date().toISOString();
    data.recommendations.unshift(recObj);
    this.save(data);
    return recObj;
  },

  getMessages() { return this.get().messages; },
  addMessage(msgObj) {
    const data = this.get();
    msgObj.id = (data.messages.length ? Math.max(...data.messages.map(m => m.id)) : 0) + 1;
    msgObj.timestamp = new Date().toISOString();
    data.messages.push(msgObj);
    this.save(data);
    return msgObj;
  }
};

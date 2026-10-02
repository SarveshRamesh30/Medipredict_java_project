/**
 * MediPredict Web Application Controller
 * Complete Front Intro Landing Page + Register + Login + Patient/Doctor Portals
 * South Indian Chennai Healthcare Edition (Clean Generic Profiles)
 */

const App = {
  currentUser: null,
  currentView: "landing", // Default to front intro page
  selectedSymptoms: new Set([15, 20, 4]),
  activeDoctorId: 1,

  init() {
    this.initTheme();
    this.loadSession();
    this.bindEvents();
    this.renderTopNav();
    this.renderSidebar();
    this.navigate(this.currentUser ? (this.currentUser.role === "DOCTOR" ? "doctor-dashboard" : "patient-dashboard") : "landing");
  },

  initTheme() {
    const theme = localStorage.getItem("medipredict_theme") || "light";
    document.documentElement.setAttribute("data-theme", theme);
    const icon = document.querySelector("#themeToggleBtn i");
    if (icon) icon.className = theme === "dark" ? "fa-solid fa-sun" : "fa-solid fa-moon";
  },

  toggleTheme() {
    const cur = document.documentElement.getAttribute("data-theme");
    const next = cur === "dark" ? "light" : "dark";
    document.documentElement.setAttribute("data-theme", next);
    localStorage.setItem("medipredict_theme", next);
    const icon = document.querySelector("#themeToggleBtn i");
    if (icon) icon.className = next === "dark" ? "fa-solid fa-sun" : "fa-solid fa-moon";
  },

  loadSession() {
    const saved = localStorage.getItem("medipredict_session");
    if (saved) {
      const parsed = JSON.parse(saved);
      this.currentUser = DB.findUserById(parsed.id) || DB.findUserByEmail(parsed.email);
    } else {
      this.currentUser = null; // Start on front intro page
    }
  },

  setSession(user) {
    this.currentUser = user;
    if (user) {
      localStorage.setItem("medipredict_session", JSON.stringify({ id: user.id, email: user.email }));
    } else {
      localStorage.removeItem("medipredict_session");
    }
    this.renderTopNav();
    this.renderSidebar();
    this.navigate(user ? (user.role === "DOCTOR" ? "doctor-dashboard" : "patient-dashboard") : "landing");
  },

  logout() {
    this.setSession(null);
    this.showToast("Signed out safely.", "info");
    this.navigate("landing");
  },

  bindEvents() {
    document.getElementById("brandLogo").addEventListener("click", () => {
      this.navigate(this.currentUser ? (this.currentUser.role === "DOCTOR" ? "doctor-dashboard" : "patient-dashboard") : "landing");
    });

    document.getElementById("themeToggleBtn").addEventListener("click", () => this.toggleTheme());

    const demoBtn = document.getElementById("demoTriggerBtn");
    const demoMenu = document.getElementById("demoMenu");
    demoBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      demoMenu.classList.toggle("show");
    });
    document.addEventListener("click", () => demoMenu.classList.remove("show"));

    document.querySelectorAll(".demo-item").forEach(item => {
      item.addEventListener("click", () => {
        const email = item.getAttribute("data-login");
        const u = DB.findUserByEmail(email);
        if (u) {
          this.setSession(u);
          this.showToast(`Logged in as ${u.fullName}`, "success");
        }
      });
    });

    document.getElementById("closeBookingModal")?.addEventListener("click", () => this.closeModal());
    document.getElementById("cancelBookingBtn")?.addEventListener("click", () => this.closeModal());
    document.getElementById("appointmentBookingForm")?.addEventListener("submit", (e) => this.handleBooking(e));
  },

  renderTopNav() {
    const container = document.getElementById("topUserInfo");
    if (!container) return;

    if (!this.currentUser) {
      container.innerHTML = `
        <button class="btn btn-white-outline btn-sm" id="navToLoginBtn">Login</button>
        <button class="btn btn-teal btn-sm" id="navToRegisterBtn">Register</button>
      `;
      document.getElementById("navToLoginBtn")?.addEventListener("click", () => this.navigate("login"));
      document.getElementById("navToRegisterBtn")?.addEventListener("click", () => this.navigate("register"));
      return;
    }

    const u = this.currentUser;
    const isPatient = u.role === "PATIENT";
    const displayName = u.fullName.toLowerCase().replace("dr. ", "");

    container.innerHTML = `
      <span class="user-name-text">${displayName}</span>
      <span class="user-role-badge ${isPatient ? 'role-badge-patient' : 'role-badge-doctor'}">${u.role}</span>
      <button class="btn-logout" id="logoutActionBtn">Logout</button>
    `;

    document.getElementById("logoutActionBtn")?.addEventListener("click", () => this.logout());
  },

  renderSidebar() {
    const sidebar = document.getElementById("appSidebar");
    const nav = document.getElementById("sidebarNav");
    const title = document.getElementById("sidebarRoleTitle");
    if (!sidebar || !nav) return;

    if (!this.currentUser) {
      sidebar.style.display = "none";
      return;
    } else {
      sidebar.style.display = "flex";
    }

    const role = this.currentUser.role;
    title.textContent = role === "DOCTOR" ? "DOCTOR PORTAL..." : "PATIENT PORTAL...";

    if (role === "PATIENT") {
      nav.innerHTML = `
        <a class="sidebar-nav-item ${this.currentView === 'patient-dashboard' ? 'active' : ''}" data-view="patient-dashboard">
          <i class="fa-solid fa-table-columns"></i> Dashboard
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'symptom-check' ? 'active' : ''}" data-view="symptom-check">
          <i class="fa-solid fa-stethoscope"></i> Symptom Predictor
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'history' ? 'active' : ''}" data-view="history">
          <i class="fa-solid fa-clock-rotate-left"></i> Prediction History
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'doctors' ? 'active' : ''}" data-view="doctors">
          <i class="fa-solid fa-user-doctor"></i> Find Doctors
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'appointments' ? 'active' : ''}" data-view="appointments">
          <i class="fa-solid fa-calendar-check"></i> My Appointments
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'advice' ? 'active' : ''}" data-view="advice">
          <i class="fa-solid fa-file-medical"></i> Doctor Advice
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'chat' ? 'active' : ''}" data-view="chat">
          <i class="fa-solid fa-comments"></i> Messages
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'profile' ? 'active' : ''}" data-view="profile">
          <i class="fa-solid fa-user"></i> My Profile
        </a>
      `;
    } else {
      nav.innerHTML = `
        <a class="sidebar-nav-item ${this.currentView === 'doctor-dashboard' ? 'active' : ''}" data-view="doctor-dashboard">
          <i class="fa-solid fa-chart-line"></i> Dashboard
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'doctor-appointments' ? 'active' : ''}" data-view="doctor-appointments">
          <i class="fa-solid fa-calendar-check"></i> Appointments
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'doctor-patients' ? 'active' : ''}" data-view="doctor-patients">
          <i class="fa-solid fa-hospital-user"></i> Patient Records
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'chat' ? 'active' : ''}" data-view="chat">
          <i class="fa-solid fa-comments"></i> Messages
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'profile' ? 'active' : ''}" data-view="profile">
          <i class="fa-solid fa-user-doctor"></i> Practice Profile
        </a>
      `;
    }

    nav.querySelectorAll("[data-view]").forEach(link => {
      link.addEventListener("click", () => {
        this.navigate(link.getAttribute("data-view"));
      });
    });
  },

  navigate(viewName, params = {}) {
    this.currentView = viewName;
    this.renderTopNav();
    this.renderSidebar();
    const main = document.getElementById("appMain");
    window.scrollTo({ top: 0, behavior: "smooth" });

    switch (viewName) {
      case "landing":
        this.renderLanding(main);
        break;
      case "login":
        this.renderLogin(main);
        break;
      case "register":
        this.renderRegister(main);
        break;
      case "patient-dashboard":
        this.renderPatientDashboard(main);
        break;
      case "doctor-dashboard":
        this.renderDoctorDashboard(main);
        break;
      case "symptom-check":
        this.renderSymptomCheck(main);
        break;
      case "prediction-result":
        this.renderPredictionResult(main, params.result);
        break;
      case "history":
        this.renderPredictionHistory(main);
        break;
      case "doctors":
        this.renderDoctorDirectory(main);
        break;
      case "appointments":
        this.renderAppointments(main);
        break;
      case "doctor-appointments":
        this.renderDoctorAppointments(main);
        break;
      case "doctor-patients":
        this.renderDoctorPatients(main);
        break;
      case "advice":
        this.renderDoctorAdvice(main);
        break;
      case "chat":
        this.renderChat(main, params.docId);
        break;
      case "profile":
        this.renderProfile(main);
        break;
      default:
        this.renderLanding(main);
    }
  },

  // -------------------------------------------------------------
  // View 0: Front Intro Landing Page
  // -------------------------------------------------------------
  renderLanding(container) {
    container.innerHTML = `
      <div style="max-width: 1100px; margin: 0 auto;">
        <!-- Hero Banner with Gradient -->
        <div style="background: linear-gradient(135deg, #0f766e 0%, #0e7490 100%); border-radius: var(--radius-lg); padding: 3rem 3.5rem; color: white; box-shadow: var(--shadow-md); margin-bottom: 2.5rem;">
          <div style="display: inline-block; background: rgba(204, 251, 241, 0.2); color: #ccfbf1; font-size: 0.75rem; font-weight: 800; padding: 4px 12px; border-radius: var(--radius-sm); letter-spacing: 0.8px; margin-bottom: 12px; text-transform: uppercase;">
            SMART HEALTHCARE PREDICTION & CONSULTATION
          </div>
          <h1 style="font-size: 2.5rem; font-weight: 800; line-height: 1.2; margin-bottom: 12px; letter-spacing: -0.5px;">
            Understand Your Symptoms.<br>Take the Right Next Step.
          </h1>
          <p style="font-size: 1.05rem; color: #e6fffa; line-height: 1.6; max-width: 800px; margin-bottom: 1.75rem;">
            MediPredict is an intelligent clinical assistance platform that helps you assess preliminary medical conditions based on reported symptoms, securely manage your clinical history, and schedule verified consultations with specialist doctors in Chennai.
          </p>

          <div style="display: flex; gap: 1rem; flex-wrap: wrap; margin-bottom: 1.75rem;">
            <button class="btn btn-white-outline" style="background: white; color: #0f766e; border-color: white; font-size: 1rem; padding: 0.75rem 1.5rem;" id="landingGetStartedBtn">
              Get Started Free ➢
            </button>
            <button class="btn btn-white-outline" style="color: white; border-color: rgba(255,255,255,0.7); font-size: 1rem; padding: 0.75rem 1.5rem;" id="landingSignInBtn">
              Sign In to Portal
            </button>
          </div>

          <!-- Highlight Stat Pills -->
          <div style="display: flex; gap: 12px; flex-wrap: wrap; padding-top: 10px; border-top: 1px solid rgba(255, 255, 255, 0.15); font-size: 0.85rem; color: #ccfbf1; font-weight: 700;">
            <span>🩺 27+ Symptoms</span>
            <span>•</span>
            <span>📋 12+ Conditions</span>
            <span>•</span>
            <span>👨‍⚕️ Chennai Specialists</span>
            <span>•</span>
            <span>🔒 Secure Role-Based Auth</span>
          </div>
        </div>

        <!-- Core Healthcare Capabilities -->
        <div style="margin-bottom: 2.5rem;">
          <h2 style="font-size: 1.5rem; font-weight: 800; color: #0f766e; margin-bottom: 1.25rem;">
            Core Healthcare Capabilities
          </h2>

          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.25rem;">
            <div class="stat-box">
              <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 6px;">
                🩺 Symptom-Based Prediction
              </h3>
              <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                Select from categorized symptoms to receive an instant, explainable match score, confidence rating, and differential clinical insights.
              </p>
            </div>

            <div class="stat-box">
              <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 6px;">
                👨‍⚕️ Specialist Consultations
              </h3>
              <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                Browse accredited physicians across Apollo, Kauvery, MIOT, and MGM Healthcare in Chennai with experience and fees upfront in ₹ INR.
              </p>
            </div>

            <div class="stat-box">
              <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 6px;">
                📅 Appointment Management
              </h3>
              <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                Book consultations with automated duplicate slot collision prevention, real-time status tracking, and doctor confirmations.
              </p>
            </div>

            <div class="stat-box">
              <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 6px;">
                📜 Clinical Prediction History
              </h3>
              <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                Access a complete chronological timeline of your previous symptom assessments with immutable record keeping.
              </p>
            </div>

            <div class="stat-box">
              <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 6px;">
                📋 Doctor Care Advice
              </h3>
              <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                Receive personalized clinical guidance, lifestyle instructions, and scheduled follow-up dates directly on your dashboard.
              </p>
            </div>

            <div class="stat-box">
              <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 6px;">
                💬 Secure Doctor Messaging
              </h3>
              <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                Consult securely with your attending physician through real-time consultation messaging with instant query replies.
              </p>
            </div>
          </div>
        </div>

        <!-- Quick Access Banner -->
        <div style="background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.5rem 2rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
          <div>
            <h3 style="font-size: 1.15rem; font-weight: 800; color: #0f766e; margin-bottom: 2px;">Ready to understand your symptoms?</h3>
            <p style="color: var(--text-secondary); font-size: 0.85rem;">Create a free account or test instantly with a demo profile.</p>
          </div>
          <div style="display: flex; gap: 10px;">
            <button class="btn btn-teal" id="ctaRegisterBtn"><i class="fa-solid fa-user-plus"></i> Create Account</button>
            <button class="btn btn-white-outline" id="ctaLoginBtn"><i class="fa-solid fa-right-to-bracket"></i> Sign In</button>
          </div>
        </div>
      </div>
    `;

    document.getElementById("landingGetStartedBtn")?.addEventListener("click", () => this.navigate("register"));
    document.getElementById("landingSignInBtn")?.addEventListener("click", () => this.navigate("login"));
    document.getElementById("ctaRegisterBtn")?.addEventListener("click", () => this.navigate("register"));
    document.getElementById("ctaLoginBtn")?.addEventListener("click", () => this.navigate("login"));
  },

  // -------------------------------------------------------------
  // View 1: Register (Blank Placeholders)
  // -------------------------------------------------------------
  renderRegister(container) {
    container.innerHTML = `
      <div style="max-width: 720px; margin: 0 auto; background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 2.25rem 2.5rem; box-shadow: var(--shadow-sm);">
        <h2 style="font-size: 1.7rem; font-weight: 800; color: #0f766e; margin-bottom: 4px;">Create Your MediPredict Account</h2>
        <p style="color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1.5rem;">Join our smart healthcare platform as a Patient or Doctor</p>

        <form id="userRegisterForm">
          <!-- Role Radio Selector -->
          <div style="display: flex; gap: 2rem; margin-bottom: 1.5rem; font-weight: 700; font-size: 0.95rem;">
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
              <input type="radio" name="accountRole" value="PATIENT" checked id="radioPatient">
              <span>I am a Patient</span>
            </label>
            <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
              <input type="radio" name="accountRole" value="DOCTOR" id="radioDoctor">
              <span>I am a Doctor</span>
            </label>
          </div>

          <div class="form-group">
            <label>Full Name</label>
            <input type="text" id="regFullName" class="form-input" placeholder="Enter your full name" required>
          </div>

          <div class="form-group">
            <label>Email Address</label>
            <input type="email" id="regEmail" class="form-input" placeholder="e.g. yourname@example.com" required>
          </div>

          <div class="form-group">
            <label>Phone Number</label>
            <input type="tel" id="regPhone" class="form-input" placeholder="e.g. +91 9876543210" required>
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" id="regPassword" class="form-input" placeholder="Create a secure password (min 6 characters)" required>
          </div>

          <!-- Patient Fields -->
          <div id="patientSpecificFields">
            <div class="form-group">
              <label>Date of Birth (YYYY-MM-DD)</label>
              <input type="text" id="regDob" class="form-input" placeholder="YYYY-MM-DD (e.g. 2000-01-15)">
            </div>

            <div class="form-group">
              <label>Gender</label>
              <select id="regGender" class="form-select">
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div class="form-group">
              <label>Blood Group</label>
              <select id="regBlood" class="form-select">
                <option value="Unknown">Unknown</option>
                <option value="A+">A+</option>
                <option value="A-">A-</option>
                <option value="B+">B+</option>
                <option value="B-">B-</option>
                <option value="O+" selected>O+</option>
                <option value="O-">O-</option>
                <option value="AB+">AB+</option>
                <option value="AB-">AB-</option>
              </select>
            </div>

            <div class="form-group">
              <label>Medical History / Known Allergies</label>
              <input type="text" id="regHistory" class="form-input" placeholder="Optional: e.g. Seasonal allergies, asthma, none">
            </div>

            <div class="form-group">
              <label>Emergency Contact</label>
              <input type="text" id="regEmergency" class="form-input" placeholder="e.g. +91 98401 23456">
            </div>
          </div>

          <!-- Doctor Fields (hidden by default) -->
          <div id="doctorSpecificFields" style="display: none;">
            <div class="form-group">
              <label>Medical Specialization</label>
              <input type="text" id="regSpec" class="form-input" placeholder="e.g. General Physician, Neurologist, Cardiologist">
            </div>
            <div class="form-group">
              <label>Hospital / Clinic Name (Chennai)</label>
              <input type="text" id="regHospital" class="form-input" placeholder="e.g. Apollo Hospitals, Kauvery Hospital, MIOT">
            </div>
            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:12px;">
              <div class="form-group">
                <label>Experience (Years)</label>
                <input type="number" id="regExp" class="form-input" placeholder="10" min="1">
              </div>
              <div class="form-group">
                <label>Consultation Fee (₹ INR)</label>
                <input type="number" id="regFee" class="form-input" placeholder="500" min="100">
              </div>
            </div>
            <div class="form-group">
              <label>Professional Bio</label>
              <textarea id="regBio" class="form-textarea" rows="2" placeholder="Brief summary of clinical expertise..."></textarea>
            </div>
          </div>

          <button type="submit" class="btn btn-teal" style="width: 100%; padding: 0.75rem; margin-top: 1rem; font-size: 1rem;">
            Create Account
          </button>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; font-size: 0.885rem; color: var(--text-secondary);">
          Already have an account? <a href="#" id="toLoginFromReg" style="color: #0f766e; font-weight: 700;">Sign in</a>
        </div>
      </div>
    `;

    const rPatient = document.getElementById("radioPatient");
    const rDoctor = document.getElementById("radioDoctor");
    const pFields = document.getElementById("patientSpecificFields");
    const dFields = document.getElementById("doctorSpecificFields");

    rPatient.addEventListener("change", () => {
      pFields.style.display = "block";
      dFields.style.display = "none";
    });
    rDoctor.addEventListener("change", () => {
      pFields.style.display = "none";
      dFields.style.display = "block";
    });

    document.getElementById("userRegisterForm").addEventListener("submit", (e) => {
      e.preventDefault();
      const role = rDoctor.checked ? "DOCTOR" : "PATIENT";
      const fullName = document.getElementById("regFullName").value.trim();
      const email = document.getElementById("regEmail").value.trim();
      const phone = document.getElementById("regPhone").value.trim();

      const newUser = {
        fullName: role === "DOCTOR" && !fullName.startsWith("Dr.") ? `Dr. ${fullName}, MD` : fullName,
        email,
        phone,
        role
      };

      if (role === "PATIENT") {
        const patients = DB.getPatients();
        newUser.patientProfile = {
          id: patients.length + 1,
          dob: document.getElementById("regDob").value.trim(),
          gender: document.getElementById("regGender").value,
          bloodGroup: document.getElementById("regBlood").value,
          medicalHistory: document.getElementById("regHistory").value.trim(),
          emergencyContact: document.getElementById("regEmergency").value.trim()
        };
      } else {
        const doctors = DB.getDoctors();
        newUser.doctorProfile = {
          id: doctors.length + 1,
          specialization: document.getElementById("regSpec").value.trim() || "General Physician",
          hospital: document.getElementById("regHospital").value.trim() || "Chennai Medical Care",
          experienceYears: parseInt(document.getElementById("regExp").value) || 10,
          consultationFee: parseFloat(document.getElementById("regFee").value) || 500,
          bio: document.getElementById("regBio").value.trim() || "Dedicated medical practitioner in Chennai.",
          availabilityHours: "9:00 AM - 5:00 PM"
        };
      }

      const created = DB.addUser(newUser);
      this.setSession(created);
      this.showToast("Account created successfully! Welcome to MediPredict.", "success");
    });

    document.getElementById("toLoginFromReg").addEventListener("click", (e) => {
      e.preventDefault();
      this.navigate("login");
    });
  },

  // -------------------------------------------------------------
  // View 2: Login (Blank Placeholders)
  // -------------------------------------------------------------
  renderLogin(container) {
    container.innerHTML = `
      <div style="max-width: 480px; margin: 2rem auto; background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 2.25rem 2.5rem; box-shadow: var(--shadow-sm);">
        <h2 style="font-size: 1.6rem; font-weight: 800; color: #0f766e; margin-bottom: 4px;">Sign in to MediPredict</h2>
        <p style="color: var(--text-secondary); font-size: 0.885rem; margin-bottom: 1.5rem;">Enter your credentials to access your health portal</p>

        <form id="userLoginForm">
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" id="loginEmailField" class="form-input" placeholder="e.g. patient@example.com" required>
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" id="loginPasswordField" class="form-input" placeholder="••••••••" required>
          </div>

          <button type="submit" class="btn btn-teal" style="width: 100%; padding: 0.75rem; margin-top: 0.5rem; font-size: 0.95rem;">
            Sign In
          </button>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; font-size: 0.885rem; color: var(--text-secondary);">
          Don't have an account? <a href="#" id="toRegFromLogin" style="color: #0f766e; font-weight: 700;">Create one</a>
        </div>

        <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border-color); text-align: center;">
          <small style="color: var(--text-muted); font-weight: 700; text-transform: uppercase;">Quick Demo Login:</small>
          <div style="display: flex; gap: 8px; justify-content: center; margin-top: 8px; flex-wrap: wrap;">
            <button class="btn btn-white-outline btn-sm" id="quickLoginPatient">Patient Demo</button>
            <button class="btn btn-white-outline btn-sm" id="quickLoginDoctor">Doctor Demo (Dr. Radhika)</button>
          </div>
        </div>
      </div>
    `;

    document.getElementById("userLoginForm").addEventListener("submit", (e) => {
      e.preventDefault();
      const email = document.getElementById("loginEmailField").value.trim();
      const u = DB.findUserByEmail(email);
      if (u) {
        this.setSession(u);
      } else {
        this.showToast("Account not found. Use a demo button or register.", "error");
      }
    });

    document.getElementById("toRegFromLogin").addEventListener("click", (e) => {
      e.preventDefault();
      this.navigate("register");
    });

    document.getElementById("quickLoginPatient").addEventListener("click", () => {
      const u = DB.findUserByEmail("patient@medipredict.com");
      if (u) this.setSession(u);
    });

    document.getElementById("quickLoginDoctor").addEventListener("click", () => {
      const u = DB.findUserByEmail("dr.radhika@kauveryhospital.com");
      if (u) this.setSession(u);
    });
  },

  // -------------------------------------------------------------
  // View 3: Patient Dashboard
  // -------------------------------------------------------------
  renderPatientDashboard(container) {
    const user = this.currentUser || { fullName: "Patient" };
    const name = user.fullName.toLowerCase().replace("dr. ", "");

    const patId = user.patientProfile?.id || 1;
    const predictions = DB.getPredictions().filter(p => p.patientId === patId);
    const appointments = DB.getAppointments().filter(a => a.patientId === patId);
    const recommendations = DB.getRecommendations().filter(r => r.patientId === patId);
    const unreadCount = DB.getMessages().filter(m => m.receiverId === user.id && !m.isRead).length;

    const latestPred = predictions[0] || {
      predictedConditionName: "Sinusitis",
      createdAt: "2026-09-19T06:54:00.000Z",
      symptomsReported: ["Abdominal Pain", "Body Pain", "Cold", "Dizziness", "Fever", "Headache", "Runny Nose", "Vomiting"]
    };

    container.innerHTML = `
      <div class="welcome-header-card">
        <div>
          <h1 class="welcome-title">Welcome back, ${name}</h1>
          <p class="welcome-sub">Track your preliminary symptom insights and manage doctor consultations effortlessly.</p>
        </div>
        <div class="welcome-actions">
          <button class="btn btn-teal" id="startNewPredBtn"><i class="fa-solid fa-plus"></i> Start New Prediction</button>
          <button class="btn btn-white-outline" id="findDocsBtn"><i class="fa-solid fa-user-doctor"></i> Find Doctors</button>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-box">
          <div class="stat-box-label">TOTAL PREDICTIONS</div>
          <div class="stat-box-value val-teal">${Math.max(predictions.length, 1)}</div>
          <div class="stat-box-desc">Symptom assessments</div>
        </div>
        <div class="stat-box">
          <div class="stat-box-label">UPCOMING APPOINTMENTS</div>
          <div class="stat-box-value val-blue">${appointments.filter(a => a.status === 'CONFIRMED' || a.status === 'PENDING').length}</div>
          <div class="stat-box-desc">No active bookings</div>
        </div>
        <div class="stat-box">
          <div class="stat-box-label">DOCTOR ADVICE</div>
          <div class="stat-box-value val-teal" style="font-size:1.5rem;">${recommendations.length > 0 ? recommendations.length : 'None'}</div>
          <div class="stat-box-desc">Clinical recommendations</div>
        </div>
        <div class="stat-box">
          <div class="stat-box-label">UNREAD MESSAGES</div>
          <div class="stat-box-value val-blue">${unreadCount}</div>
          <div class="stat-box-desc">Doctor communications</div>
        </div>
      </div>

      <div class="summary-cards-row">
        <!-- Latest Prediction -->
        <div class="summary-card">
          <div>
            <div class="summary-card-header">
              <span class="summary-card-title">Latest Prediction</span>
              <span class="badge-match">High Match</span>
            </div>
            <div class="summary-card-body">
              <strong>Possible Condition: ${latestPred.predictedConditionName}</strong><br>
              <small style="color:var(--text-muted); display:block; margin: 4px 0 8px;">Date: ${new Date(latestPred.createdAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</small>
              <div style="font-size:0.825rem; color:var(--text-secondary);">
                <strong>Symptoms:</strong> ${(latestPred.symptomsReported || []).slice(0, 8).join(', ')}...
              </div>
            </div>
          </div>
          <div class="summary-card-footer">
            <a class="summary-card-link" id="viewFullHistLink">View Full History &rarr;</a>
          </div>
        </div>

        <!-- Upcoming Appointment -->
        <div class="summary-card">
          <div>
            <div class="summary-card-header">
              <span class="summary-card-title">Upcoming Appointment</span>
            </div>
            <div class="summary-card-body">
              ${appointments.length > 0 ? `
                <strong>${appointments[0].doctorName}</strong><br>
                <small>${appointments[0].appointmentDate} at ${appointments[0].appointmentTime}</small>
                <p style="margin-top:6px; font-size:0.825rem;">${appointments[0].notes}</p>
              ` : `
                No upcoming appointments scheduled.<br>Connect with our expert physicians in Chennai.
              `}
            </div>
          </div>
          <div class="summary-card-footer">
            <a class="summary-card-link" id="bookApptLink">Book Appointment &rarr;</a>
          </div>
        </div>

        <!-- Doctor Recommendation -->
        <div class="summary-card">
          <div>
            <div class="summary-card-header">
              <span class="summary-card-title">Doctor Recommendation</span>
            </div>
            <div class="summary-card-body">
              ${recommendations.length > 0 ? `
                <strong>${recommendations[0].doctorName}</strong> (${recommendations[0].doctorSpec})<br>
                <p style="margin-top:6px; font-size:0.825rem;">${recommendations[0].clinicalAdvice}</p>
              ` : `
                No personalized doctor recommendations received yet.
              `}
            </div>
          </div>
          <div class="summary-card-footer">
            <a class="summary-card-link" id="viewAdviceLink">View All Advice &rarr;</a>
          </div>
        </div>
      </div>
    `;

    document.getElementById("startNewPredBtn")?.addEventListener("click", () => this.navigate("symptom-check"));
    document.getElementById("findDocsBtn")?.addEventListener("click", () => this.navigate("doctors"));
    document.getElementById("viewFullHistLink")?.addEventListener("click", () => this.navigate("history"));
    document.getElementById("bookApptLink")?.addEventListener("click", () => this.navigate("doctors"));
    document.getElementById("viewAdviceLink")?.addEventListener("click", () => this.navigate("advice"));
  },

  // -------------------------------------------------------------
  // View 4: Symptom Predictor
  // -------------------------------------------------------------
  renderSymptomCheck(container) {
    const allSymptoms = DB.getSymptoms();
    const categories = ["All Categories", "General", "Neurological", "Respiratory", "ENT", "Digestive", "Cardiovascular", "Musculoskeletal", "Dermatological"];

    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Symptom-Based Medical Condition Prediction</h2>
        <p style="color:var(--text-secondary); font-size:0.9rem;">Select all symptoms you are currently experiencing to evaluate preliminary condition correlations.</p>

        <!-- Search & Filter Row -->
        <div class="filter-bar-row">
          <div class="filter-item">
            <span>Search:</span>
            <input type="text" id="symSearch" class="filter-input" placeholder="Type symptom (e.g. Headache, Cough, Fever)...">
          </div>
          <div class="filter-item">
            <span>Category:</span>
            <select id="symCategory" class="filter-select">
              ${categories.map(c => `<option value="${c}">${c}</option>`).join('')}
            </select>
          </div>
        </div>

        <!-- Symptoms Selection Panel -->
        <div class="symptoms-panel-card">
          <div class="symptoms-panel-top">
            <span class="symptoms-panel-title">Click Symptoms to Select / Deselect (Multi-Select Enabled)</span>
            <span class="symptoms-counter-badge" id="symSelectedCounter">${this.selectedSymptoms.size} symptoms selected</span>
          </div>

          <div class="chips-cloud" id="chipsCloudContainer">
            <!-- Chips rendered dynamically -->
          </div>
        </div>

        <!-- Bottom Action Bar -->
        <div class="predictor-bottom-actions">
          <div style="font-size:0.885rem; font-weight:700; white-space:nowrap;">Optional Observations / Duration Notes:</div>
          <input type="text" id="durationNotes" class="duration-notes-input" placeholder="e.g. Symptoms started 2 days ago after exposure to cold weather">
          <button class="btn btn-white-outline" id="clearAllChipsBtn">Clear All</button>
          <button class="btn btn-teal" id="analyzeSymptomsBtn">
            <i class="fa-solid fa-stethoscope"></i> Analyze & Predict Condition
          </button>
        </div>
      </div>
    `;

    let activeCat = "All Categories";
    let activeQuery = "";

    const renderChips = () => {
      const cloud = document.getElementById("chipsCloudContainer");
      const filtered = allSymptoms.filter(s => {
        const matchesCat = activeCat === "All Categories" || s.category.toLowerCase() === activeCat.toLowerCase();
        const matchesQ = !activeQuery || s.name.toLowerCase().includes(activeQuery.toLowerCase());
        return matchesCat && matchesQ;
      });

      cloud.innerHTML = filtered.map(s => {
        const isSelected = this.selectedSymptoms.has(s.id);
        return `
          <button class="symptom-toggle-chip ${isSelected ? 'selected' : ''}" data-id="${s.id}">
            <i class="fa-solid ${isSelected ? 'fa-check' : 'fa-plus'}"></i>
            <span>${s.name}</span>
          </button>
        `;
      }).join('');

      cloud.querySelectorAll(".symptom-toggle-chip").forEach(chip => {
        chip.addEventListener("click", () => {
          const id = parseInt(chip.dataset.id);
          if (this.selectedSymptoms.has(id)) {
            this.selectedSymptoms.delete(id);
          } else {
            this.selectedSymptoms.add(id);
          }
          document.getElementById("symSelectedCounter").textContent = `${this.selectedSymptoms.size} symptoms selected`;
          renderChips();
        });
      });
    };

    document.getElementById("symSearch").addEventListener("input", (e) => {
      activeQuery = e.target.value.trim();
      renderChips();
    });

    document.getElementById("symCategory").addEventListener("change", (e) => {
      activeCat = e.target.value;
      renderChips();
    });

    document.getElementById("clearAllChipsBtn").addEventListener("click", () => {
      this.selectedSymptoms.clear();
      document.getElementById("symSelectedCounter").textContent = `0 symptoms selected`;
      renderChips();
    });

    document.getElementById("analyzeSymptomsBtn").addEventListener("click", () => {
      if (this.selectedSymptoms.size === 0) {
        this.showToast("Please select at least one symptom.", "error");
        return;
      }
      const result = PredictionEngine.predict(Array.from(this.selectedSymptoms));
      const notes = document.getElementById("durationNotes").value.trim();

      if (this.currentUser && this.currentUser.role === "PATIENT") {
        const patId = this.currentUser.patientProfile?.id || 1;
        DB.addPrediction({
          patientId: patId,
          patientName: this.currentUser.fullName,
          predictedConditionId: result.primaryCondition.id || 0,
          predictedConditionName: result.primaryCondition.name,
          matchScore: result.matchScore,
          confidenceLevel: result.confidenceLevel,
          riskLevel: result.riskLevel,
          notes: notes || `Reported symptoms: ${result.inputSymptoms.map(s => s.name).join(', ')}`,
          symptomsReported: result.inputSymptoms.map(s => s.name)
        });
      }

      this.navigate("prediction-result", { result });
    });

    renderChips();
  },

  // -------------------------------------------------------------
  // View 5: Prediction Results
  // -------------------------------------------------------------
  renderPredictionResult(container, result) {
    if (!result) {
      this.navigate("symptom-check");
      return;
    }

    const primary = result.primaryCondition;
    const now = new Date();
    const dateFormatted = now.toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }).toLowerCase();

    container.innerHTML = `
      <div>
        <div style="background: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.5rem 1.75rem; margin-bottom: 1.5rem; box-shadow: var(--shadow-sm);">
          <div style="font-size: 0.72rem; font-weight: 800; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px;">
            PRELIMINARY SYMPTOM ASSESSMENT
          </div>
          <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
            <div>
              <h2 style="font-size: 1.85rem; font-weight: 800; color: #0f766e; margin-bottom: 4px;">
                Possible Condition: ${primary.name}
              </h2>
              <small style="color: var(--text-muted); font-size: 0.85rem;">Assessment Date: ${dateFormatted}</small>
            </div>
            <div style="display: flex; gap: 8px; flex-wrap: wrap;">
              <span style="background: #ccfbf1; color: #0f766e; font-weight: 800; padding: 4px 10px; border-radius: var(--radius-sm); font-size: 0.8rem;">
                Match Score: ${result.matchScore}%
              </span>
              <span style="background: ${result.confidenceLevel === 'High' ? '#dcfce7' : '#fee2e2'}; color: ${result.confidenceLevel === 'High' ? '#15803d' : '#b91c1c'}; font-weight: 800; padding: 4px 10px; border-radius: var(--radius-sm); font-size: 0.8rem;">
                ${result.confidenceLevel} Confidence
              </span>
              <span style="background: #fef3c7; color: #b45309; font-weight: 800; padding: 4px 10px; border-radius: var(--radius-sm); font-size: 0.8rem;">
                Risk: ${result.riskLevel}
              </span>
            </div>
          </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem;">
          <!-- Left: Analysis & Reported Symptoms -->
          <div class="stat-box">
            <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 1rem;">Analysis & Reported Symptoms</h3>
            <p style="font-size: 0.885rem; color: var(--text-primary); line-height: 1.6; margin-bottom: 1rem;">
              Based on the ${result.inputSymptoms.length} symptom(s) you reported, the prediction engine identified a strong correlation with ${primary.name} (${result.matchScore}% match index).
            </p>
            <p style="font-size: 0.885rem; color: var(--text-primary); line-height: 1.6; margin-bottom: 1rem;">
              <strong>Key matching indicators included:</strong> ${(result.matchedSymptoms || []).map(s => s.name).join(', ') || result.inputSymptoms.map(s => s.name).join(', ')}.
            </p>
            <p style="font-size: 0.885rem; color: var(--text-secondary); line-height: 1.6; margin-bottom: 1.5rem;">
              <strong>Medical Overview:</strong> ${primary.description}
            </p>

            <div style="background: var(--bg-main); padding: 0.9rem; border-radius: var(--radius-sm); border: 1px solid var(--border-color);">
              <strong style="font-size: 0.825rem; color: var(--text-primary); display: block; margin-bottom: 6px;">Differential Possibilities Considered:</strong>
              ${result.differentialDiagnoses.map(d => `
                <div style="font-size: 0.825rem; color: var(--text-secondary); margin-bottom: 3px;">
                  • ${d.condition.name} (${d.score}% match)
                </div>
              `).join('')}
            </div>
          </div>

          <!-- Right: General Information & Precautions -->
          <div class="stat-box">
            <h3 style="font-size: 1.05rem; font-weight: 800; color: #0f766e; margin-bottom: 1rem;">General Information & Precautions</h3>
            <ul style="padding-left: 18px; font-size: 0.885rem; color: var(--text-secondary); line-height: 1.8;">
              ${result.precautions.map(p => `<li>${p.replace(/•\s*/, '')}</li>`).join('')}
            </ul>
          </div>
        </div>

        <!-- Yellow Disclaimer Banner -->
        <div style="background: #fef3c7; border: 1px solid #fde68a; border-radius: var(--radius-md); padding: 0.9rem 1.25rem; color: #92400e; font-size: 0.825rem; line-height: 1.5; margin-bottom: 1.5rem;">
          <strong>⚠️ IMPORTANT NON-DIAGNOSTIC MEDICAL DISCLAIMER:</strong><br>
          MediPredict provides preliminary symptom-based information for educational and informational purposes only. It does not provide a medical diagnosis or replace professional medical advice. Please consult a qualified healthcare professional for proper evaluation and treatment.
        </div>

        <!-- Bottom Action Buttons -->
        <div style="display: flex; gap: 12px; justify-content: flex-end; flex-wrap: wrap;">
          <button class="btn btn-teal" id="btnConsultDoctorAction">
            <i class="fa-solid fa-user-doctor"></i> Consult / Book Doctor
          </button>
          <button class="btn btn-white-outline" id="btnViewHistoryAction">
            <i class="fa-solid fa-clock-rotate-left"></i> View History
          </button>
          <button class="btn btn-white-outline" id="btnStartNewPredAction">
            <i class="fa-solid fa-plus"></i> Start New Prediction
          </button>
        </div>
      </div>
    `;

    document.getElementById("btnConsultDoctorAction").addEventListener("click", () => this.navigate("doctors"));
    document.getElementById("btnViewHistoryAction").addEventListener("click", () => this.navigate("history"));
    document.getElementById("btnStartNewPredAction").addEventListener("click", () => this.navigate("symptom-check"));
  },

  // -------------------------------------------------------------
  // View 6: Doctor Directory (Chennai Doctors)
  // -------------------------------------------------------------
  renderDoctorDirectory(container) {
    const doctors = DB.getDoctors();
    const specialties = ["All Specializations", "General Physician", "Neurologist", "Pulmonologist", "Gastroenterologist", "Cardiologist"];

    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Doctor Directory & Consultation Specialists</h2>
        <p style="color:var(--text-secondary); font-size:0.9rem; margin-bottom: 1.5rem;">Browse accredited medical physicians and schedule in-person or virtual consultations.</p>

        <!-- Search & Specialty Filter Row -->
        <div class="filter-bar-row">
          <div class="filter-item">
            <span>Search Doctors:</span>
            <input type="text" id="docSearchBox" class="filter-input" placeholder="Search by name, hospital, or specialty...">
          </div>
          <div class="filter-item">
            <span>Specialty:</span>
            <select id="docSpecSelect" class="filter-select">
              ${specialties.map(s => `<option value="${s}">${s}</option>`).join('')}
            </select>
          </div>
        </div>

        <!-- 2x2 Grid for Doctor Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(420px, 1fr)); gap: 1.5rem;" id="doctorCardsGrid">
          <!-- Rendered dynamically -->
        </div>
      </div>
    `;

    const renderGrid = (query = "", spec = "All Specializations") => {
      const grid = document.getElementById("doctorCardsGrid");
      const filtered = doctors.filter(d => {
        const p = d.doctorProfile || {};
        const matchesSpec = spec === "All Specializations" || p.specialization === spec;
        const matchesQuery = !query || d.fullName.toLowerCase().includes(query.toLowerCase()) || p.hospital.toLowerCase().includes(query.toLowerCase()) || p.specialization.toLowerCase().includes(query.toLowerCase());
        return matchesSpec && matchesQuery;
      });

      grid.innerHTML = filtered.map(d => {
        const p = d.doctorProfile || {};
        return `
          <div class="stat-box" style="display:flex; flex-direction:column; justify-content:space-between; min-height:220px;">
            <div>
              <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:6px;">
                <h3 style="font-size:1.15rem; font-weight:800; color:#0f766e;">${d.fullName}</h3>
                <span style="background:#e0f2fe; color:#0284c7; font-size:0.75rem; font-weight:800; padding:3px 8px; border-radius:var(--radius-sm);">
                  ${p.specialization}
                </span>
              </div>
              <div style="font-size:0.85rem; color:var(--text-primary); margin-bottom:4px;">
                <i class="fa-solid fa-hospital" style="color:#0f766e; margin-right:4px;"></i> ${p.hospital}
              </div>
              <div style="font-size:0.8rem; color:var(--text-muted); margin-bottom:10px;">
                ${p.experienceYears} yrs experience &nbsp;|&nbsp; Fee: <strong>₹${p.consultationFee}</strong> &nbsp;|&nbsp; ${p.availabilityHours}
              </div>
              <p style="font-size:0.85rem; color:var(--text-secondary); line-height:1.4;">
                ${p.bio}
              </p>
            </div>

            <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:1.25rem;">
              <button class="btn btn-white-outline btn-sm chat-doc-action" data-userid="${d.id}">
                <i class="fa-solid fa-comment-dots"></i> Message
              </button>
              <button class="btn btn-teal btn-sm book-doc-action" data-docid="${p.id}">
                <i class="fa-solid fa-calendar-check"></i> Book Appointment...
              </button>
            </div>
          </div>
        `;
      }).join('');

      grid.querySelectorAll(".book-doc-action").forEach(b => {
        b.addEventListener("click", () => {
          if (!this.currentUser) {
            this.showToast("Please sign in to schedule an appointment.", "info");
            this.navigate("login");
            return;
          }
          this.openModal(b.dataset.docid);
        });
      });

      grid.querySelectorAll(".chat-doc-action").forEach(b => {
        b.addEventListener("click", () => {
          if (!this.currentUser) {
            this.showToast("Please sign in to message doctors.", "info");
            this.navigate("login");
            return;
          }
          this.navigate("chat", { docId: parseInt(b.dataset.userid) });
        });
      });
    };

    document.getElementById("docSearchBox").addEventListener("input", (e) => {
      renderGrid(e.target.value.trim(), document.getElementById("docSpecSelect").value);
    });

    document.getElementById("docSpecSelect").addEventListener("change", (e) => {
      renderGrid(document.getElementById("docSearchBox").value.trim(), e.target.value);
    });

    renderGrid();
  },

  // -------------------------------------------------------------
  // View 7: Messaging / Chat
  // -------------------------------------------------------------
  renderChat(container, selectedDocUserId = null) {
    const doctors = DB.getDoctors();
    let currentDoc = doctors.find(d => d.id === selectedDocUserId) || doctors[0];
    const user = this.currentUser || { id: 1, fullName: "Patient" };

    const messages = DB.getMessages().filter(m => 
      (m.senderId === user.id && m.receiverId === currentDoc.id) ||
      (m.senderId === currentDoc.id && m.receiverId === user.id)
    );

    container.innerHTML = `
      <div class="chat-layout-grid">
        <!-- Left Doctor List -->
        <div class="chat-doctor-list">
          <div class="chat-doctor-list-header">
            <i class="fa-solid fa-comments"></i> Conversations
          </div>
          <div>
            ${doctors.map(d => {
              const p = d.doctorProfile || {};
              const isActive = d.id === currentDoc.id;
              return `
                <div class="chat-doctor-item ${isActive ? 'active' : ''}" data-docuserid="${d.id}">
                  <strong>${d.fullName} (${p.specialization})</strong>
                  <small>${p.hospital}</small>
                </div>
              `;
            }).join('')}
          </div>
        </div>

        <!-- Right Conversation Panel -->
        <div class="chat-conversation-panel">
          <div class="chat-panel-header">
            <h3>${currentDoc.fullName}</h3>
            <small>${currentDoc.doctorProfile?.specialization || 'General Physician'} • ${currentDoc.doctorProfile?.hospital || 'Chennai'}</small>
          </div>

          <div class="chat-message-history" id="chatHistoryBox">
            ${messages.length > 0 ? messages.map(m => {
              const isMe = m.senderId === user.id;
              const time = new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
              return `
                <div class="chat-msg-row ${isMe ? 'chat-msg-sent' : 'chat-msg-received'}">
                  <span class="chat-msg-sender">${isMe ? 'You' : currentDoc.fullName}</span>
                  <div class="chat-msg-bubble ${isMe ? 'chat-msg-bubble-sent' : 'chat-msg-bubble-received'}">
                    ${m.content}
                  </div>
                  <span class="chat-msg-time">${time}</span>
                </div>
              `;
            }).join('') : `
              <div class="chat-msg-row chat-msg-sent">
                <span class="chat-msg-sender">You</span>
                <div class="chat-msg-bubble chat-msg-bubble-sent">
                  hello doctor i have severe fever
                </div>
                <span class="chat-msg-time">06:55 am</span>
              </div>
            `}
          </div>

          <form class="chat-input-row" id="chatSubmitForm">
            <input type="text" id="chatInputMsg" class="chat-input-field" placeholder="Type your medical query or response..." required autocomplete="off">
            <button type="submit" class="btn btn-teal">Send <i class="fa-solid fa-paper-plane"></i></button>
          </form>
        </div>
      </div>
    `;

    container.querySelectorAll(".chat-doctor-item").forEach(item => {
      item.addEventListener("click", () => {
        this.renderChat(container, parseInt(item.dataset.docuserid));
      });
    });

    document.getElementById("chatSubmitForm").addEventListener("submit", (e) => {
      e.preventDefault();
      const input = document.getElementById("chatInputMsg");
      const text = input.value.trim();
      if (!text) return;

      DB.addMessage({
        senderId: user.id,
        receiverId: currentDoc.id,
        senderName: user.fullName,
        receiverName: currentDoc.fullName,
        content: text,
        isRead: 0
      });

      input.value = "";
      this.renderChat(container, currentDoc.id);
    });
  },

  // Prediction History, Appointments, Doctor Advice & Profile
  renderPredictionHistory(container) {
    const user = this.currentUser || { patientProfile: { id: 1 } };
    const preds = DB.getPredictions().filter(p => p.patientId === (user.patientProfile?.id || 1));

    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Prediction History</h2>
        <p style="color:var(--text-secondary); margin-bottom:1.5rem;">Review all previous symptom evaluations and risk logs.</p>
        <div class="stat-box">
          ${preds.map(p => `
            <div style="padding:1rem; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong>${p.predictedConditionName}</strong> (${p.matchScore}% Match)
                <div style="font-size:0.8rem; color:var(--text-muted);">${(p.symptomsReported || []).join(', ')}</div>
              </div>
              <span class="badge-match">${new Date(p.createdAt).toLocaleDateString()}</span>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  },

  renderAppointments(container) {
    const appts = DB.getAppointments();
    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">My Consultation Appointments</h2>
        <div class="stat-box" style="margin-top:1.5rem;">
          ${appts.map(a => `
            <div style="padding:1rem; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong>${a.doctorName}</strong> (${a.doctorSpec})
                <div style="font-size:0.85rem; color:var(--text-secondary);">${a.appointmentDate} at ${a.appointmentTime} - ${a.notes}</div>
              </div>
              <span class="badge-match">${a.status}</span>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  },

  renderDoctorAdvice(container) {
    const recs = DB.getRecommendations();
    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Doctor Recommendations & Care Advice</h2>
        <div class="stat-box" style="margin-top:1.5rem;">
          ${recs.length > 0 ? recs.map(r => `
            <div style="padding:1.2rem; border-bottom:1px solid var(--border-subtle);">
              <h3 style="font-size:1.05rem; font-weight:800; color:#0f766e;">${r.doctorName} (${r.doctorSpec})</h3>
              <p style="font-size:0.9rem; color:var(--text-primary); margin:6px 0;"><strong>Advice:</strong> ${r.clinicalAdvice}</p>
              <p style="font-size:0.85rem; color:var(--text-secondary);"><strong>Dietary/Lifestyle:</strong> ${r.dietaryLifestyle}</p>
              <small style="color:var(--text-muted); display:block; margin-top:4px;">Follow-up by: ${r.followUpDate}</small>
            </div>
          `).join('') : `
            <div style="text-align:center; padding:2rem 0; color:var(--text-muted);">
              No clinical recommendations received yet.
            </div>
          `}
        </div>
      </div>
    `;
  },

  renderProfile(container) {
    const u = this.currentUser || {};
    container.innerHTML = `
      <div style="max-width:600px;">
        <h2 class="predictor-header-title">Health Profile & Account Settings</h2>
        <div class="stat-box" style="margin-top:1.5rem;">
          <div class="form-group">
            <label>Full Name</label>
            <input type="text" class="form-input" value="${u.fullName || ''}">
          </div>
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" class="form-input" value="${u.email || ''}" disabled>
          </div>
          <div class="form-group">
            <label>Phone</label>
            <input type="tel" class="form-input" value="${u.phone || ''}">
          </div>
          <button class="btn btn-teal" style="margin-top:10px;">Update Profile</button>
        </div>
      </div>
    `;
  },

  renderDoctorDashboard(container) {
    const u = this.currentUser || {};
    const appts = DB.getAppointments();
    container.innerHTML = `
      <div class="welcome-header-card">
        <div>
          <h1 class="welcome-title">Physician Clinical Portal</h1>
          <p class="welcome-sub">${u.fullName} • ${u.doctorProfile?.specialization || 'Physician'} (${u.doctorProfile?.hospital || 'Chennai'})</p>
        </div>
      </div>
      <div class="stat-box">
        <h3 style="font-size:1.1rem; font-weight:800; color:#0f766e; margin-bottom:1rem;">Consultation Queue</h3>
        ${appts.map(a => `
          <div style="padding:0.75rem 0; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center;">
            <span><strong>${a.patientName}</strong> – ${a.appointmentDate} (${a.appointmentTime})</span>
            <span class="badge-match">${a.status}</span>
          </div>
        `).join('')}
      </div>
    `;
  },

  renderDoctorAppointments(container) { this.renderAppointments(container); },
  renderDoctorPatients(container) {
    const patients = DB.getPatients();
    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Patient Electronic Health Records</h2>
        <div class="stat-box" style="margin-top:1.5rem;">
          ${patients.map(p => `
            <div style="padding:1rem; border-bottom:1px solid var(--border-subtle);">
              <strong>${p.fullName}</strong> (${p.patientProfile?.gender || 'Patient'} • Blood: ${p.patientProfile?.bloodGroup || 'O+'})
              <div style="font-size:0.85rem; color:var(--text-secondary); margin-top:4px;">${p.patientProfile?.medicalHistory || 'None'}</div>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  },

  // Modal Handlers
  openModal(docId = null) {
    const select = document.getElementById("bookDoctorSelect");
    const doctors = DB.getDoctors();
    select.innerHTML = doctors.map(d => `<option value="${d.doctorProfile?.id || d.id}" ${d.doctorProfile?.id === parseInt(docId) ? 'selected' : ''}>${d.fullName} – ${d.doctorProfile?.specialization} (₹${d.doctorProfile?.consultationFee})</option>`).join('');
    
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    document.getElementById("bookDateInput").value = tomorrow.toISOString().split("T")[0];
    
    document.getElementById("bookingModal").classList.add("active");
  },

  closeModal() {
    document.getElementById("bookingModal").classList.remove("active");
  },

  handleBooking(e) {
    e.preventDefault();
    const docId = parseInt(document.getElementById("bookDoctorSelect").value);
    const date = document.getElementById("bookDateInput").value;
    const time = document.getElementById("bookTimeSelect").value;
    const notes = document.getElementById("bookNotesInput").value.trim();

    const doc = DB.getDoctorByDoctorId(docId);
    DB.addAppointment({
      patientId: this.currentUser?.patientProfile?.id || 1,
      patientName: this.currentUser?.fullName || "Patient",
      doctorId: docId,
      doctorName: doc?.fullName || "Physician",
      doctorSpec: doc?.doctorProfile?.specialization || "General",
      appointmentDate: date,
      appointmentTime: time,
      notes: notes,
      status: "CONFIRMED"
    });

    this.closeModal();
    this.showToast("Appointment confirmed with doctor in Chennai!", "success");
    this.navigate("patient-dashboard");
  },

  showToast(msg, type = "info") {
    const c = document.getElementById("toastContainer");
    if (!c) return;
    const t = document.createElement("div");
    t.className = `toast toast-${type}`;
    t.textContent = msg;
    c.appendChild(t);
    setTimeout(() => t.remove(), 3000);
  }
};

document.addEventListener("DOMContentLoaded", () => App.init());

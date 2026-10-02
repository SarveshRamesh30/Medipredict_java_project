/**
 * MediPredict Web Application Controller
 * Next-Gen Clinical Platform (Desktop & Mobile Dynamic Responsive Edition)
 * South Indian Chennai Healthcare System
 */

const App = {
  currentUser: null,
  currentView: "landing",
  selectedSymptoms: new Set([15, 20, 4]), // Default sample symptoms (Fever, Headache, Cold)
  activeDoctorId: 1,
  mobileChatOpen: false,

  init() {
    this.initTheme();
    this.loadSession();
    this.bindEvents();
    this.renderTopNav();
    this.renderSidebar();
    this.renderMobileBottomNav();
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
      try {
        const parsed = JSON.parse(saved);
        this.currentUser = DB.findUserById(parsed.id) || DB.findUserByEmail(parsed.email);
      } catch (e) {
        this.currentUser = null;
      }
    } else {
      this.currentUser = null;
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
    this.renderMobileBottomNav();
    this.navigate(user ? (user.role === "DOCTOR" ? "doctor-dashboard" : "patient-dashboard") : "landing");
  },

  logout() {
    this.setSession(null);
    this.showToast("Signed out safely.", "info");
    this.navigate("landing");
  },

  toggleMobileDrawer(forceState) {
    const sidebar = document.getElementById("appSidebar");
    const backdrop = document.getElementById("sidebarBackdrop");
    if (!sidebar || !backdrop) return;

    const isOpen = typeof forceState === "boolean" ? forceState : !sidebar.classList.contains("drawer-open");
    if (isOpen) {
      sidebar.classList.add("drawer-open");
      backdrop.classList.add("active");
    } else {
      sidebar.classList.remove("drawer-open");
      backdrop.classList.remove("active");
    }
  },

  closeMobileDrawer() {
    this.toggleMobileDrawer(false);
  },

  bindEvents() {
    // Brand Logo Clicks
    document.getElementById("brandLogo")?.addEventListener("click", () => {
      this.closeMobileDrawer();
      this.navigate(this.currentUser ? (this.currentUser.role === "DOCTOR" ? "doctor-dashboard" : "patient-dashboard") : "landing");
    });

    document.getElementById("mobileNavBrand")?.addEventListener("click", () => {
      this.closeMobileDrawer();
      this.navigate(this.currentUser ? (this.currentUser.role === "DOCTOR" ? "doctor-dashboard" : "patient-dashboard") : "landing");
    });

    // Mobile Drawer Toggles
    document.getElementById("mobileMenuToggleBtn")?.addEventListener("click", () => this.toggleMobileDrawer());
    document.getElementById("sidebarCloseBtn")?.addEventListener("click", () => this.closeMobileDrawer());
    document.getElementById("sidebarBackdrop")?.addEventListener("click", () => this.closeMobileDrawer());

    // Theme Toggle
    document.getElementById("themeToggleBtn")?.addEventListener("click", () => this.toggleTheme());

    // Demo Menu Dropdown
    const demoBtn = document.getElementById("demoTriggerBtn");
    const demoMenu = document.getElementById("demoMenu");
    demoBtn?.addEventListener("click", (e) => {
      e.stopPropagation();
      demoMenu?.classList.toggle("show");
    });
    document.addEventListener("click", () => demoMenu?.classList.remove("show"));

    document.querySelectorAll(".demo-item").forEach(item => {
      item.addEventListener("click", () => {
        const email = item.getAttribute("data-login");
        const u = DB.findUserByEmail(email);
        if (u) {
          this.setSession(u);
          this.showToast(`Logged in as ${u.fullName}`, "success");
        }
        demoMenu?.classList.remove("show");
      });
    });

    // Modal Events
    document.getElementById("closeBookingModal")?.addEventListener("click", () => this.closeModal());
    document.getElementById("cancelBookingBtn")?.addEventListener("click", () => this.closeModal());
    document.getElementById("appointmentBookingForm")?.addEventListener("submit", (e) => this.handleBooking(e));
  },

  renderTopNav() {
    const container = document.getElementById("topUserInfo");
    if (!container) return;

    if (!this.currentUser) {
      container.innerHTML = `
        <button class="btn btn-outline btn-sm" id="navToLoginBtn">Login</button>
        <button class="btn btn-teal btn-sm" id="navToRegisterBtn">Register</button>
      `;
      document.getElementById("navToLoginBtn")?.addEventListener("click", () => this.navigate("login"));
      document.getElementById("navToRegisterBtn")?.addEventListener("click", () => this.navigate("register"));
      return;
    }

    const u = this.currentUser;
    const isPatient = u.role === "PATIENT";
    const displayName = u.fullName.replace(/^Dr\.\s*/i, "");

    container.innerHTML = `
      <span class="user-name-text" title="${u.fullName}">${displayName}</span>
      <span class="user-role-badge ${isPatient ? 'role-badge-patient' : 'role-badge-doctor'}">${u.role}</span>
      <button class="btn-logout" id="logoutActionBtn" title="Sign Out">
        <i class="fa-solid fa-right-from-bracket"></i>
        <span>Logout</span>
      </button>
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
    title.textContent = role === "DOCTOR" ? "DOCTOR PORTAL" : "PATIENT PORTAL";

    if (role === "PATIENT") {
      nav.innerHTML = `
        <a class="sidebar-nav-item ${this.currentView === 'patient-dashboard' ? 'active' : ''}" data-view="patient-dashboard">
          <i class="fa-solid fa-table-columns"></i> <span>Dashboard</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'symptom-check' ? 'active' : ''}" data-view="symptom-check">
          <i class="fa-solid fa-stethoscope"></i> <span>Symptom Predictor</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'history' ? 'active' : ''}" data-view="history">
          <i class="fa-solid fa-clock-rotate-left"></i> <span>Prediction History</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'doctors' ? 'active' : ''}" data-view="doctors">
          <i class="fa-solid fa-user-doctor"></i> <span>Find Doctors</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'appointments' ? 'active' : ''}" data-view="appointments">
          <i class="fa-solid fa-calendar-check"></i> <span>My Appointments</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'advice' ? 'active' : ''}" data-view="advice">
          <i class="fa-solid fa-file-medical"></i> <span>Doctor Advice</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'chat' ? 'active' : ''}" data-view="chat">
          <i class="fa-solid fa-comments"></i> <span>Consultation Messages</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'profile' ? 'active' : ''}" data-view="profile">
          <i class="fa-solid fa-user"></i> <span>My Profile</span>
        </a>
      `;
    } else {
      nav.innerHTML = `
        <a class="sidebar-nav-item ${this.currentView === 'doctor-dashboard' ? 'active' : ''}" data-view="doctor-dashboard">
          <i class="fa-solid fa-chart-line"></i> <span>Dashboard</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'doctor-appointments' ? 'active' : ''}" data-view="doctor-appointments">
          <i class="fa-solid fa-calendar-check"></i> <span>Appointments</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'doctor-patients' ? 'active' : ''}" data-view="doctor-patients">
          <i class="fa-solid fa-hospital-user"></i> <span>Patient Records</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'chat' ? 'active' : ''}" data-view="chat">
          <i class="fa-solid fa-comments"></i> <span>Messages</span>
        </a>
        <a class="sidebar-nav-item ${this.currentView === 'profile' ? 'active' : ''}" data-view="profile">
          <i class="fa-solid fa-user-doctor"></i> <span>Practice Profile</span>
        </a>
      `;
    }

    nav.querySelectorAll("[data-view]").forEach(link => {
      link.addEventListener("click", () => {
        this.closeMobileDrawer();
        this.navigate(link.getAttribute("data-view"));
      });
    });
  },

  renderMobileBottomNav() {
    const bottomNav = document.getElementById("mobileBottomNav");
    if (!bottomNav) return;

    if (!this.currentUser) {
      bottomNav.innerHTML = `
        <a class="mobile-nav-tab ${this.currentView === 'landing' ? 'active' : ''}" data-bview="landing">
          <i class="fa-solid fa-house"></i>
          <span>Home</span>
        </a>
        <a class="mobile-nav-tab mobile-nav-tab-featured ${this.currentView === 'symptom-check' ? 'active' : ''}" data-bview="symptom-check">
          <div class="featured-icon-circle">
            <i class="fa-solid fa-stethoscope"></i>
          </div>
          <span>Predictor</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'doctors' ? 'active' : ''}" data-bview="doctors">
          <i class="fa-solid fa-user-doctor"></i>
          <span>Doctors</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'login' ? 'active' : ''}" data-bview="login">
          <i class="fa-solid fa-right-to-bracket"></i>
          <span>Sign In</span>
        </a>
      `;
    } else if (this.currentUser.role === "PATIENT") {
      bottomNav.innerHTML = `
        <a class="mobile-nav-tab ${this.currentView === 'patient-dashboard' ? 'active' : ''}" data-bview="patient-dashboard">
          <i class="fa-solid fa-table-columns"></i>
          <span>Dashboard</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'history' ? 'active' : ''}" data-bview="history">
          <i class="fa-solid fa-clock-rotate-left"></i>
          <span>History</span>
        </a>
        <a class="mobile-nav-tab mobile-nav-tab-featured ${this.currentView === 'symptom-check' ? 'active' : ''}" data-bview="symptom-check">
          <div class="featured-icon-circle">
            <i class="fa-solid fa-stethoscope"></i>
          </div>
          <span>Predictor</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'doctors' || this.currentView === 'appointments' ? 'active' : ''}" data-bview="doctors">
          <i class="fa-solid fa-user-doctor"></i>
          <span>Doctors</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'chat' ? 'active' : ''}" data-bview="chat">
          <i class="fa-solid fa-comments"></i>
          <span>Chat</span>
        </a>
      `;
    } else {
      // Doctor View
      bottomNav.innerHTML = `
        <a class="mobile-nav-tab ${this.currentView === 'doctor-dashboard' ? 'active' : ''}" data-bview="doctor-dashboard">
          <i class="fa-solid fa-chart-line"></i>
          <span>Dashboard</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'doctor-appointments' ? 'active' : ''}" data-bview="doctor-appointments">
          <i class="fa-solid fa-calendar-check"></i>
          <span>Queue</span>
        </a>
        <a class="mobile-nav-tab mobile-nav-tab-featured ${this.currentView === 'doctor-patients' ? 'active' : ''}" data-bview="doctor-patients">
          <div class="featured-icon-circle">
            <i class="fa-solid fa-hospital-user"></i>
          </div>
          <span>Patients</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'chat' ? 'active' : ''}" data-bview="chat">
          <i class="fa-solid fa-comments"></i>
          <span>Chat</span>
        </a>
        <a class="mobile-nav-tab ${this.currentView === 'profile' ? 'active' : ''}" data-bview="profile">
          <i class="fa-solid fa-user-doctor"></i>
          <span>Profile</span>
        </a>
      `;
    }

    bottomNav.querySelectorAll("[data-bview]").forEach(btn => {
      btn.addEventListener("click", () => {
        this.closeMobileDrawer();
        this.navigate(btn.getAttribute("data-bview"));
      });
    });
  },

  navigate(viewName, params = {}) {
    this.currentView = viewName;
    this.closeMobileDrawer();
    this.renderTopNav();
    this.renderSidebar();
    this.renderMobileBottomNav();

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
        <div class="landing-hero">
          <div class="hero-pill-badge">
            <i class="fa-solid fa-sparkles"></i> SMART HEALTHCARE PREDICTION & CONSULTATION
          </div>
          <h1 class="hero-title">
            Understand Your Symptoms.<br>Take the Right Next Step.
          </h1>
          <p class="hero-desc">
            MediPredict is an intelligent clinical assistance platform that helps you evaluate preliminary health conditions based on reported symptoms, securely manage your clinical history, and schedule verified specialist consultations in Chennai.
          </p>

          <div class="hero-actions">
            <button class="btn btn-lg" style="background:#ffffff; color:#0f766e; font-weight:800;" id="landingGetStartedBtn">
              <i class="fa-solid fa-stethoscope"></i> Check Symptoms Now
            </button>
            <button class="btn btn-lg btn-outline" style="border-color:rgba(255,255,255,0.7); color:#ffffff; background:rgba(255,255,255,0.12);" id="landingSignInBtn">
              <i class="fa-solid fa-right-to-bracket"></i> Sign In to Portal
            </button>
          </div>

          <!-- Highlight Stat Pills -->
          <div class="hero-stats-scroll">
            <div class="hero-stat-item">🩺 27+ Symptoms</div>
            <div class="hero-stat-item">📋 12+ Medical Conditions</div>
            <div class="hero-stat-item">👨‍⚕️ Chennai Specialists</div>
            <div class="hero-stat-item">🔒 Role-Based EHR</div>
          </div>
        </div>

        <!-- Core Healthcare Capabilities -->
        <div style="margin-bottom: 2.5rem;">
          <h2 class="landing-section-title">
            <i class="fa-solid fa-heart-pulse"></i> Core Healthcare Capabilities
          </h2>

          <div class="capabilities-grid">
            <div class="capability-card">
              <div class="capability-icon-wrap"><i class="fa-solid fa-stethoscope"></i></div>
              <h3 class="capability-title">Symptom-Based Prediction</h3>
              <p class="capability-desc">Select from categorized symptoms to receive an explainable match score, confidence level, and differential clinical insights.</p>
            </div>

            <div class="capability-card">
              <div class="capability-icon-wrap"><i class="fa-solid fa-user-doctor"></i></div>
              <h3 class="capability-title">Chennai Specialist Directory</h3>
              <p class="capability-desc">Browse accredited physicians across Kauvery, Apollo, MIOT, and MGM Healthcare with experience and fees upfront in ₹ INR.</p>
            </div>

            <div class="capability-card">
              <div class="capability-icon-wrap"><i class="fa-solid fa-calendar-check"></i></div>
              <h3 class="capability-title">Appointment Scheduling</h3>
              <p class="capability-desc">Book consultations with collision prevention, real-time status tracking, and instant doctor confirmation workflows.</p>
            </div>

            <div class="capability-card">
              <div class="capability-icon-wrap"><i class="fa-solid fa-clock-rotate-left"></i></div>
              <h3 class="capability-title">Clinical History Timeline</h3>
              <p class="capability-desc">Access a chronological timeline of previous symptom assessments and risk logs with structured electronic health tracking.</p>
            </div>

            <div class="capability-card">
              <div class="capability-icon-wrap"><i class="fa-solid fa-file-medical"></i></div>
              <h3 class="capability-title">Doctor Care Advice</h3>
              <p class="capability-desc">Receive personalized clinical prescriptions, lifestyle guidelines, and follow-up dates directly from attending doctors.</p>
            </div>

            <div class="capability-card">
              <div class="capability-icon-wrap"><i class="fa-solid fa-comments"></i></div>
              <h3 class="capability-title">Direct Messaging</h3>
              <p class="capability-desc">Consult securely with specialists via real-time messaging for follow-up questions and care coordination.</p>
            </div>
          </div>
        </div>

        <!-- Quick Access Banner -->
        <div class="landing-cta-banner">
          <div>
            <h3 style="font-size:1.15rem; font-weight:800; color:var(--teal-primary); margin-bottom:4px;">Ready to check your symptoms?</h3>
            <p style="color:var(--text-secondary); font-size:0.875rem;">Start an assessment instantly or sign in to your medical dashboard.</p>
          </div>
          <div style="display:flex; gap:10px; flex-wrap:wrap;">
            <button class="btn btn-teal" id="ctaRegisterBtn"><i class="fa-solid fa-user-plus"></i> Create Account</button>
            <button class="btn btn-outline" id="ctaLoginBtn"><i class="fa-solid fa-right-to-bracket"></i> Sign In</button>
          </div>
        </div>
      </div>
    `;

    document.getElementById("landingGetStartedBtn")?.addEventListener("click", () => this.navigate("symptom-check"));
    document.getElementById("landingSignInBtn")?.addEventListener("click", () => this.navigate("login"));
    document.getElementById("ctaRegisterBtn")?.addEventListener("click", () => this.navigate("register"));
    document.getElementById("ctaLoginBtn")?.addEventListener("click", () => this.navigate("login"));
  },

  // -------------------------------------------------------------
  // View 1: Register
  // -------------------------------------------------------------
  renderRegister(container) {
    container.innerHTML = `
      <div class="auth-card">
        <h2 class="auth-title">Create MediPredict Account</h2>
        <p class="auth-sub">Join our smart clinical assistance network in Chennai</p>

        <form id="userRegisterForm">
          <!-- Role Selector -->
          <div class="role-selector-pills">
            <label class="role-pill-option active" id="labelPatientRadio">
              <input type="radio" name="accountRole" value="PATIENT" checked id="radioPatient">
              <span>I am a Patient</span>
            </label>
            <label class="role-pill-option" id="labelDoctorRadio">
              <input type="radio" name="accountRole" value="DOCTOR" id="radioDoctor">
              <span>I am a Doctor</span>
            </label>
          </div>

          <div class="form-group">
            <label>Full Name</label>
            <input type="text" id="regFullName" class="form-input" placeholder="e.g. Ramesh Kumar" required>
          </div>

          <div class="form-group">
            <label>Email Address</label>
            <input type="email" id="regEmail" class="form-input" placeholder="e.g. ramesh.kumar@example.com" required>
          </div>

          <div class="form-group">
            <label>Phone Number</label>
            <input type="tel" id="regPhone" class="form-input" placeholder="e.g. +91 98401 23456" required>
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" id="regPassword" class="form-input" placeholder="Create a secure password" required minlength="6">
          </div>

          <!-- Patient Fields -->
          <div id="patientSpecificFields">
            <div class="form-row-2col">
              <div class="form-group">
                <label>Date of Birth</label>
                <input type="date" id="regDob" class="form-input" value="1995-06-15">
              </div>
              <div class="form-group">
                <label>Gender</label>
                <select id="regGender" class="form-select">
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>

            <div class="form-row-2col">
              <div class="form-group">
                <label>Blood Group</label>
                <select id="regBlood" class="form-select">
                  <option value="O+" selected>O+</option>
                  <option value="O-">O-</option>
                  <option value="A+">A+</option>
                  <option value="A-">A-</option>
                  <option value="B+">B+</option>
                  <option value="B-">B-</option>
                  <option value="AB+">AB+</option>
                  <option value="AB-">AB-</option>
                </select>
              </div>
              <div class="form-group">
                <label>Emergency Contact</label>
                <input type="tel" id="regEmergency" class="form-input" placeholder="e.g. +91 98401 99999">
              </div>
            </div>

            <div class="form-group">
              <label>Medical History / Known Allergies</label>
              <input type="text" id="regHistory" class="form-input" placeholder="e.g. Seasonal allergy, Mild hypertension, None">
            </div>
          </div>

          <!-- Doctor Fields -->
          <div id="doctorSpecificFields" style="display: none;">
            <div class="form-group">
              <label>Medical Specialization</label>
              <input type="text" id="regSpec" class="form-input" placeholder="e.g. General Physician, Neurologist, Cardiologist">
            </div>
            <div class="form-group">
              <label>Hospital / Clinic Name (Chennai)</label>
              <input type="text" id="regHospital" class="form-input" placeholder="e.g. Apollo Hospitals, Kauvery Hospital, MIOT">
            </div>
            <div class="form-row-2col">
              <div class="form-group">
                <label>Experience (Years)</label>
                <input type="number" id="regExp" class="form-input" placeholder="10" min="1" value="12">
              </div>
              <div class="form-group">
                <label>Consultation Fee (₹ INR)</label>
                <input type="number" id="regFee" class="form-input" placeholder="600" min="100" value="600">
              </div>
            </div>
            <div class="form-group">
              <label>Professional Bio</label>
              <textarea id="regBio" class="form-textarea" rows="2" placeholder="Brief summary of clinical expertise..."></textarea>
            </div>
          </div>

          <button type="submit" class="btn btn-teal btn-w100" style="margin-top:0.75rem; padding:0.75rem;">
            <i class="fa-solid fa-user-plus"></i> Complete Registration
          </button>
        </form>

        <div style="margin-top:1.5rem; text-align:center; font-size:0.885rem; color:var(--text-secondary);">
          Already have an account? <a href="#" id="toLoginFromReg" style="color:var(--teal-primary); font-weight:700;">Sign in</a>
        </div>
      </div>
    `;

    const rPatient = document.getElementById("radioPatient");
    const rDoctor = document.getElementById("radioDoctor");
    const lPatient = document.getElementById("labelPatientRadio");
    const lDoctor = document.getElementById("labelDoctorRadio");
    const pFields = document.getElementById("patientSpecificFields");
    const dFields = document.getElementById("doctorSpecificFields");

    rPatient.addEventListener("change", () => {
      lPatient.classList.add("active");
      lDoctor.classList.remove("active");
      pFields.style.display = "block";
      dFields.style.display = "none";
    });

    rDoctor.addEventListener("change", () => {
      lDoctor.classList.add("active");
      lPatient.classList.remove("active");
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
          dob: document.getElementById("regDob").value.trim() || "1995-01-01",
          gender: document.getElementById("regGender").value,
          bloodGroup: document.getElementById("regBlood").value,
          medicalHistory: document.getElementById("regHistory").value.trim() || "None",
          emergencyContact: document.getElementById("regEmergency").value.trim() || phone
        };
      } else {
        const doctors = DB.getDoctors();
        newUser.doctorProfile = {
          id: doctors.length + 1,
          specialization: document.getElementById("regSpec").value.trim() || "General Physician",
          hospital: document.getElementById("regHospital").value.trim() || "Chennai Medical Care",
          experienceYears: parseInt(document.getElementById("regExp").value) || 10,
          consultationFee: parseFloat(document.getElementById("regFee").value) || 500,
          bio: document.getElementById("regBio").value.trim() || "Dedicated medical specialist in Chennai.",
          availabilityHours: "9:00 AM - 5:00 PM"
        };
      }

      const created = DB.addUser(newUser);
      this.setSession(created);
      this.showToast("Account created successfully! Welcome to MediPredict.", "success");
    });

    document.getElementById("toLoginFromReg")?.addEventListener("click", (e) => {
      e.preventDefault();
      this.navigate("login");
    });
  },

  // -------------------------------------------------------------
  // View 2: Login
  // -------------------------------------------------------------
  renderLogin(container) {
    container.innerHTML = `
      <div class="auth-card" style="max-width: 480px;">
        <h2 class="auth-title">Sign In to MediPredict</h2>
        <p class="auth-sub">Access your clinical portal, predictions, and consultations</p>

        <form id="userLoginForm">
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" id="loginEmailField" class="form-input" placeholder="e.g. patient@medipredict.com" required>
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" id="loginPasswordField" class="form-input" placeholder="••••••••" required>
          </div>

          <button type="submit" class="btn btn-teal btn-w100" style="margin-top:0.5rem; padding:0.75rem;">
            <i class="fa-solid fa-right-to-bracket"></i> Sign In
          </button>
        </form>

        <div style="margin-top:1.5rem; text-align:center; font-size:0.885rem; color:var(--text-secondary);">
          Don't have an account? <a href="#" id="toRegFromLogin" style="color:var(--teal-primary); font-weight:700;">Create one</a>
        </div>

        <div style="margin-top:1.5rem; padding-top:1.25rem; border-top:1px solid var(--border-color); text-align:center;">
          <small style="color:var(--text-muted); font-weight:800; text-transform:uppercase; letter-spacing:0.5px; display:block; margin-bottom:8px;">
            1-Click Demo Profiles:
          </small>
          <div style="display:flex; gap:8px; justify-content:center; flex-wrap:wrap;">
            <button class="btn btn-outline btn-sm" id="quickLoginPatient">
              <i class="fa-solid fa-user"></i> Patient (Ramesh)
            </button>
            <button class="btn btn-outline btn-sm" id="quickLoginDoctor">
              <i class="fa-solid fa-user-doctor"></i> Doctor (Dr. Radhika)
            </button>
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
        this.showToast(`Welcome back, ${u.fullName}!`, "success");
      } else {
        this.showToast("Account not found. Click one of the demo buttons below.", "error");
      }
    });

    document.getElementById("toRegFromLogin")?.addEventListener("click", (e) => {
      e.preventDefault();
      this.navigate("register");
    });

    document.getElementById("quickLoginPatient")?.addEventListener("click", () => {
      const u = DB.findUserByEmail("patient@medipredict.com");
      if (u) {
        this.setSession(u);
        this.showToast(`Logged in as ${u.fullName}`, "success");
      }
    });

    document.getElementById("quickLoginDoctor")?.addEventListener("click", () => {
      const u = DB.findUserByEmail("dr.radhika@kauveryhospital.com");
      if (u) {
        this.setSession(u);
        this.showToast(`Logged in as ${u.fullName}`, "success");
      }
    });
  },

  // -------------------------------------------------------------
  // View 3: Patient Dashboard
  // -------------------------------------------------------------
  renderPatientDashboard(container) {
    const user = this.currentUser || { fullName: "Patient" };
    const name = user.fullName.replace(/^Dr\.\s*/i, "");
    const patId = user.patientProfile?.id || 1;

    const predictions = DB.getPredictions().filter(p => p.patientId === patId);
    const appointments = DB.getAppointments().filter(a => a.patientId === patId);
    const recommendations = DB.getRecommendations().filter(r => r.patientId === patId);
    const unreadCount = DB.getMessages().filter(m => m.receiverId === user.id && !m.isRead).length;

    const latestPred = predictions[0] || {
      predictedConditionName: "Sinusitis",
      matchScore: 88,
      createdAt: new Date().toISOString(),
      symptomsReported: ["Headache", "Fever", "Cold", "Runny Nose"]
    };

    container.innerHTML = `
      <div>
        <div class="welcome-header-card">
          <div>
            <h1 class="welcome-title">Welcome back, ${name}</h1>
            <p class="welcome-sub">Manage your symptom predictions, records, and specialist consultations in Chennai.</p>
          </div>
          <div class="welcome-actions">
            <button class="btn btn-teal" id="startNewPredBtn">
              <i class="fa-solid fa-stethoscope"></i> Start New Prediction
            </button>
            <button class="btn btn-white-outline" id="findDocsBtn">
              <i class="fa-solid fa-user-doctor"></i> Find Doctors
            </button>
          </div>
        </div>

        <div class="stats-row">
          <div class="stat-box">
            <div class="stat-box-label">TOTAL PREDICTIONS</div>
            <div class="stat-box-value val-teal">${Math.max(predictions.length, 1)}</div>
            <div class="stat-box-desc">Symptom evaluations</div>
          </div>
          <div class="stat-box">
            <div class="stat-box-label">ACTIVE APPOINTMENTS</div>
            <div class="stat-box-value val-blue">${appointments.filter(a => a.status === 'CONFIRMED' || a.status === 'PENDING').length}</div>
            <div class="stat-box-desc">${appointments.length > 0 ? 'Upcoming Chennai visits' : 'No active bookings'}</div>
          </div>
          <div class="stat-box">
            <div class="stat-box-label">DOCTOR CARE ADVICE</div>
            <div class="stat-box-value val-cyan">${recommendations.length > 0 ? recommendations.length : '1'}</div>
            <div class="stat-box-desc">Clinical recommendations</div>
          </div>
          <div class="stat-box">
            <div class="stat-box-label">MESSAGES</div>
            <div class="stat-box-value val-purple">${unreadCount > 0 ? unreadCount : '0'}</div>
            <div class="stat-box-desc">Physician chat alerts</div>
          </div>
        </div>

        <div class="summary-cards-row">
          <!-- Latest Prediction -->
          <div class="summary-card">
            <div>
              <div class="summary-card-header">
                <span class="summary-card-title">Latest Prediction</span>
                <span class="badge-match">${latestPred.matchScore || 85}% Match</span>
              </div>
              <div class="summary-card-body">
                <strong>Condition: ${latestPred.predictedConditionName}</strong>
                <small style="color:var(--text-muted); display:block; margin:4px 0 8px;">
                  Date: ${new Date(latestPred.createdAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}
                </small>
                <div>
                  <strong>Reported:</strong> ${(latestPred.symptomsReported || []).slice(0, 6).join(', ')}...
                </div>
              </div>
            </div>
            <div class="summary-card-footer">
              <a class="summary-card-link" id="viewFullHistLink">View Prediction History &rarr;</a>
            </div>
          </div>

          <!-- Upcoming Appointment -->
          <div class="summary-card">
            <div>
              <div class="summary-card-header">
                <span class="summary-card-title">Upcoming Appointment</span>
                ${appointments.length > 0 ? `<span class="badge-match">${appointments[0].status}</span>` : ''}
              </div>
              <div class="summary-card-body">
                ${appointments.length > 0 ? `
                  <strong>${appointments[0].doctorName}</strong> (${appointments[0].doctorSpec})<br>
                  <small style="color:var(--text-muted); display:block; margin:4px 0 6px;">📅 ${appointments[0].appointmentDate} at ${appointments[0].appointmentTime}</small>
                  <p style="font-size:0.85rem;">${appointments[0].notes}</p>
                ` : `
                  No appointments booked.<br>Consult top Chennai physicians at Kauvery, Apollo, or MIOT.
                `}
              </div>
            </div>
            <div class="summary-card-footer">
              <a class="summary-card-link" id="bookApptLink">${appointments.length > 0 ? 'Manage Appointments &rarr;' : 'Book Appointment &rarr;'}</a>
            </div>
          </div>

          <!-- Doctor Recommendation -->
          <div class="summary-card">
            <div>
              <div class="summary-card-header">
                <span class="summary-card-title">Doctor Advice</span>
              </div>
              <div class="summary-card-body">
                ${recommendations.length > 0 ? `
                  <strong>${recommendations[0].doctorName}</strong><br>
                  <p style="margin-top:6px; font-size:0.85rem;">${recommendations[0].clinicalAdvice}</p>
                ` : `
                  <strong>Dr. Radhika Sundaram, MD</strong><br>
                  <p style="margin-top:6px; font-size:0.85rem;">Stay hydrated, maintain 8 hours rest, and monitor temperature if fever exceeds 100°F.</p>
                `}
              </div>
            </div>
            <div class="summary-card-footer">
              <a class="summary-card-link" id="viewAdviceLink">View Care Guidelines &rarr;</a>
            </div>
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
        <h2 class="predictor-header-title">Symptom-Based Condition Predictor</h2>
        <p style="color:var(--text-secondary); font-size:0.92rem;">Select all symptoms you are currently experiencing to compute instant clinical correlations.</p>

        <!-- Search & Filter Row -->
        <div class="filter-bar-row">
          <div class="filter-item">
            <span><i class="fa-solid fa-magnifying-glass"></i> Search:</span>
            <input type="text" id="symSearch" class="filter-input" placeholder="Search symptom (e.g. Headache, Cough, Fever)...">
          </div>
          <div class="filter-item">
            <span><i class="fa-solid fa-layer-group"></i> Category:</span>
            <select id="symCategory" class="filter-select">
              ${categories.map(c => `<option value="${c}">${c}</option>`).join('')}
            </select>
          </div>
        </div>

        <!-- Preset Quick Combos -->
        <div style="font-size:0.75rem; font-weight:800; color:var(--text-muted); text-transform:uppercase; margin-bottom:4px; letter-spacing:0.5px;">
          Quick Test Combos:
        </div>
        <div class="preset-pills-row">
          <button class="preset-pill" data-combo="15,20,4">🤒 Flu & Cold Combo</button>
          <button class="preset-pill" data-combo="4,22,6,11">🧠 Migraine & Vertigo</button>
          <button class="preset-pill" data-combo="1,2,7,21">🤢 Gastritis & Acid Reflux</button>
          <button class="preset-pill" data-combo="13,14,15,16">🫁 Bronchitis & Cough</button>
          <button class="preset-pill" data-combo="10,12,18">🫀 Chest Discomfort & Palpitation</button>
        </div>

        <!-- Symptoms Selection Panel -->
        <div class="symptoms-panel-card">
          <div class="symptoms-panel-top">
            <span class="symptoms-panel-title">Click Symptoms to Select / Deselect</span>
            <span class="symptoms-counter-badge" id="symSelectedCounter">${this.selectedSymptoms.size} symptoms selected</span>
          </div>

          <div class="chips-cloud" id="chipsCloudContainer">
            <!-- Chips rendered dynamically -->
          </div>
        </div>

        <!-- Bottom Action Bar -->
        <div class="predictor-bottom-actions">
          <input type="text" id="durationNotes" class="duration-notes-input" placeholder="Optional notes: e.g. Started 2 days ago after travel">
          <button class="btn btn-outline" id="clearAllChipsBtn">
            <i class="fa-solid fa-trash-can"></i> Clear All
          </button>
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
      if (!cloud) return;

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

    document.getElementById("symSearch")?.addEventListener("input", (e) => {
      activeQuery = e.target.value.trim();
      renderChips();
    });

    document.getElementById("symCategory")?.addEventListener("change", (e) => {
      activeCat = e.target.value;
      renderChips();
    });

    document.querySelectorAll(".preset-pill").forEach(btn => {
      btn.addEventListener("click", () => {
        const ids = btn.dataset.combo.split(',').map(Number);
        this.selectedSymptoms = new Set(ids);
        document.getElementById("symSelectedCounter").textContent = `${this.selectedSymptoms.size} symptoms selected`;
        renderChips();
        this.showToast("Sample symptom combo loaded!", "info");
      });
    });

    document.getElementById("clearAllChipsBtn")?.addEventListener("click", () => {
      this.selectedSymptoms.clear();
      document.getElementById("symSelectedCounter").textContent = `0 symptoms selected`;
      renderChips();
    });

    document.getElementById("analyzeSymptomsBtn")?.addEventListener("click", () => {
      if (this.selectedSymptoms.size === 0) {
        this.showToast("Please select at least one symptom to evaluate.", "error");
        return;
      }
      const result = PredictionEngine.predict(Array.from(this.selectedSymptoms));
      const notes = document.getElementById("durationNotes")?.value.trim() || "";

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
          notes: notes || `Reported: ${result.inputSymptoms.map(s => s.name).join(', ')}`,
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
    const dateFormatted = now.toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });

    container.innerHTML = `
      <div>
        <div class="stat-box" style="margin-bottom:1.5rem;">
          <div style="font-size:0.72rem; font-weight:800; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px; margin-bottom:6px;">
            PRELIMINARY SYMPTOM ASSESSMENT REPORT
          </div>
          <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:1rem;">
            <div>
              <h2 style="font-size:clamp(1.4rem, 3vw, 1.85rem); font-weight:800; color:var(--teal-primary); margin-bottom:4px;">
                Possible Condition: ${primary.name}
              </h2>
              <small style="color:var(--text-muted); font-size:0.85rem;">Evaluated on ${dateFormatted}</small>
            </div>
            <div style="display:flex; gap:8px; flex-wrap:wrap;">
              <span class="badge-match" style="font-size:0.8rem; padding:5px 12px;">
                Match Score: ${result.matchScore}%
              </span>
              <span style="background:${result.confidenceLevel === 'High' ? '#dcfce7' : '#fee2e2'}; color:${result.confidenceLevel === 'High' ? '#15803d' : '#b91c1c'}; font-weight:800; padding:5px 12px; border-radius:var(--radius-full); font-size:0.8rem;">
                ${result.confidenceLevel} Confidence
              </span>
              <span style="background:#fef3c7; color:#b45309; font-weight:800; padding:5px 12px; border-radius:var(--radius-full); font-size:0.8rem;">
                Risk: ${result.riskLevel}
              </span>
            </div>
          </div>
        </div>

        <div class="results-grid-2col">
          <!-- Left: Analysis & Reported Symptoms -->
          <div class="stat-box">
            <h3 style="font-size:1.05rem; font-weight:800; color:var(--teal-primary); margin-bottom:1rem;">
              <i class="fa-solid fa-chart-pie"></i> Analysis & Matching Symptoms
            </h3>
            <p style="font-size:0.885rem; color:var(--text-primary); line-height:1.6; margin-bottom:1rem;">
              Based on the ${result.inputSymptoms.length} symptom(s) reported, the clinical engine calculated a strong correlation with <strong>${primary.name}</strong> (${result.matchScore}% match index).
            </p>
            <p style="font-size:0.885rem; color:var(--text-primary); line-height:1.6; margin-bottom:1rem;">
              <strong>Key matching indicators:</strong> ${(result.matchedSymptoms || []).map(s => s.name).join(', ') || result.inputSymptoms.map(s => s.name).join(', ')}.
            </p>
            <p style="font-size:0.885rem; color:var(--text-secondary); line-height:1.6; margin-bottom:1.5rem;">
              <strong>Clinical Overview:</strong> ${primary.description}
            </p>

            <div style="background:var(--bg-main); padding:0.95rem; border-radius:var(--radius-sm); border:1px solid var(--border-color);">
              <strong style="font-size:0.825rem; color:var(--text-primary); display:block; margin-bottom:6px;">Differential Diagnoses Evaluated:</strong>
              ${result.differentialDiagnoses.map(d => `
                <div style="font-size:0.825rem; color:var(--text-secondary); margin-bottom:4px;">
                  • ${d.condition.name} (${d.score}% match)
                </div>
              `).join('')}
            </div>
          </div>

          <!-- Right: Precautions & Recommended Care -->
          <div class="stat-box">
            <h3 style="font-size:1.05rem; font-weight:800; color:var(--teal-primary); margin-bottom:1rem;">
              <i class="fa-solid fa-shield-halved"></i> General Precautions & Care
            </h3>
            <ul style="padding-left:20px; font-size:0.885rem; color:var(--text-secondary); line-height:1.8;">
              ${result.precautions.map(p => `<li>${p.replace(/•\s*/, '')}</li>`).join('')}
            </ul>
          </div>
        </div>

        <!-- Medical Notice -->
        <div style="background:#fef3c7; border:1px solid #fde68a; border-radius:var(--radius-md); padding:1rem 1.25rem; color:#92400e; font-size:0.85rem; line-height:1.5; margin-bottom:1.5rem;">
          <strong>⚠️ NON-DIAGNOSTIC NOTICE:</strong> MediPredict results are for educational guidance and do not replace professional evaluation. Consult a licensed medical physician for medical advice and prescriptions.
        </div>

        <!-- Action Buttons -->
        <div style="display:flex; gap:10px; justify-content:flex-end; flex-wrap:wrap;">
          <button class="btn btn-teal" id="btnConsultDoctorAction">
            <i class="fa-solid fa-calendar-check"></i> Book Doctor Consultation
          </button>
          <button class="btn btn-outline" id="btnViewHistoryAction">
            <i class="fa-solid fa-clock-rotate-left"></i> View History
          </button>
          <button class="btn btn-outline" id="btnStartNewPredAction">
            <i class="fa-solid fa-rotate-left"></i> New Prediction
          </button>
        </div>
      </div>
    `;

    document.getElementById("btnConsultDoctorAction")?.addEventListener("click", () => this.navigate("doctors"));
    document.getElementById("btnViewHistoryAction")?.addEventListener("click", () => this.navigate("history"));
    document.getElementById("btnStartNewPredAction")?.addEventListener("click", () => this.navigate("symptom-check"));
  },

  // -------------------------------------------------------------
  // View 6: Doctor Directory
  // -------------------------------------------------------------
  renderDoctorDirectory(container) {
    const doctors = DB.getDoctors();
    const specialties = ["All Specializations", "General Physician", "Neurologist", "Pulmonologist", "Gastroenterologist", "Cardiologist"];

    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Chennai Specialist Directory</h2>
        <p style="color:var(--text-secondary); font-size:0.92rem;">Browse accredited physicians across leading Chennai hospitals and book verified consultations.</p>

        <!-- Search & Filter Row -->
        <div class="filter-bar-row">
          <div class="filter-item">
            <span><i class="fa-solid fa-magnifying-glass"></i> Search:</span>
            <input type="text" id="docSearchBox" class="filter-input" placeholder="Search by name, hospital, or specialty...">
          </div>
          <div class="filter-item">
            <span><i class="fa-solid fa-stethoscope"></i> Specialty:</span>
            <select id="docSpecSelect" class="filter-select">
              ${specialties.map(s => `<option value="${s}">${s}</option>`).join('')}
            </select>
          </div>
        </div>

        <!-- Doctor Cards Grid -->
        <div class="doctor-cards-grid" id="doctorCardsGrid">
          <!-- Rendered dynamically -->
        </div>
      </div>
    `;

    const renderGrid = (query = "", spec = "All Specializations") => {
      const grid = document.getElementById("doctorCardsGrid");
      if (!grid) return;

      const filtered = doctors.filter(d => {
        const p = d.doctorProfile || {};
        const matchesSpec = spec === "All Specializations" || p.specialization === spec;
        const matchesQuery = !query || d.fullName.toLowerCase().includes(query.toLowerCase()) || p.hospital.toLowerCase().includes(query.toLowerCase()) || p.specialization.toLowerCase().includes(query.toLowerCase());
        return matchesSpec && matchesQuery;
      });

      grid.innerHTML = filtered.map(d => {
        const p = d.doctorProfile || {};
        return `
          <div class="stat-box" style="display:flex; flex-direction:column; justify-content:space-between;">
            <div>
              <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px; gap:8px;">
                <h3 style="font-size:1.1rem; font-weight:800; color:var(--teal-primary);">${d.fullName}</h3>
                <span class="badge-match" style="font-size:0.75rem;">
                  ${p.specialization}
                </span>
              </div>
              <div style="font-size:0.885rem; color:var(--text-primary); margin-bottom:4px; font-weight:600;">
                <i class="fa-solid fa-hospital" style="color:var(--teal-primary); margin-right:5px;"></i> ${p.hospital}
              </div>
              <div style="font-size:0.8rem; color:var(--text-muted); margin-bottom:12px;">
                ${p.experienceYears} yrs experience &nbsp;|&nbsp; Fee: <strong>₹${p.consultationFee}</strong> &nbsp;|&nbsp; ${p.availabilityHours}
              </div>
              <p style="font-size:0.85rem; color:var(--text-secondary); line-height:1.45;">
                ${p.bio}
              </p>
            </div>

            <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:1.25rem; flex-wrap:wrap;">
              <button class="btn btn-outline btn-sm chat-doc-action" data-userid="${d.id}">
                <i class="fa-solid fa-comment-dots"></i> Message
              </button>
              <button class="btn btn-teal btn-sm book-doc-action" data-docid="${p.id}">
                <i class="fa-solid fa-calendar-plus"></i> Book Consultation
              </button>
            </div>
          </div>
        `;
      }).join('');

      grid.querySelectorAll(".book-doc-action").forEach(b => {
        b.addEventListener("click", () => {
          if (!this.currentUser) {
            this.showToast("Please sign in to schedule a consultation.", "info");
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

    document.getElementById("docSearchBox")?.addEventListener("input", (e) => {
      renderGrid(e.target.value.trim(), document.getElementById("docSpecSelect").value);
    });

    document.getElementById("docSpecSelect")?.addEventListener("change", (e) => {
      renderGrid(document.getElementById("docSearchBox").value.trim(), e.target.value);
    });

    renderGrid();
  },

  // -------------------------------------------------------------
  // View 7: Consultation Chat / Messaging (Responsive Switcher)
  // -------------------------------------------------------------
  renderChat(container, selectedDocUserId = null) {
    const doctors = DB.getDoctors();
    let currentDoc = doctors.find(d => d.id === selectedDocUserId) || doctors[0];
    const user = this.currentUser || { id: 1, fullName: "Patient" };

    const messages = DB.getMessages().filter(m => 
      (m.senderId === user.id && m.receiverId === currentDoc.id) ||
      (m.senderId === currentDoc.id && m.receiverId === user.id)
    );

    const isMobileShowingConv = this.mobileChatOpen || Boolean(selectedDocUserId);

    container.innerHTML = `
      <div>
        <div class="chat-layout-grid ${isMobileShowingConv ? 'show-conversation' : ''}" id="chatLayoutGrid">
          <!-- Left Doctor List -->
          <div class="chat-doctor-list">
            <div class="chat-doctor-list-header">
              <i class="fa-solid fa-comments"></i> Consultations
            </div>
            <div>
              ${doctors.map(d => {
                const p = d.doctorProfile || {};
                const isActive = d.id === currentDoc.id;
                return `
                  <div class="chat-doctor-item ${isActive ? 'active' : ''}" data-docuserid="${d.id}">
                    <strong>${d.fullName}</strong>
                    <small>${p.specialization} • ${p.hospital}</small>
                  </div>
                `;
              }).join('')}
            </div>
          </div>

          <!-- Right Conversation Panel -->
          <div class="chat-conversation-panel">
            <div class="chat-panel-header">
              <div class="chat-panel-header-info">
                <h3>${currentDoc.fullName}</h3>
                <small>${currentDoc.doctorProfile?.specialization || 'Physician'} • ${currentDoc.doctorProfile?.hospital || 'Chennai'}</small>
              </div>
              <button class="chat-back-btn" id="chatBackToListBtn">
                <i class="fa-solid fa-arrow-left"></i> Doctors
              </button>
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
                    Hello doctor, I recently completed my symptom check and wanted to share my preliminary findings.
                  </div>
                  <span class="chat-msg-time">09:30 AM</span>
                </div>
                <div class="chat-msg-row chat-msg-received">
                  <span class="chat-msg-sender">${currentDoc.fullName}</span>
                  <div class="chat-msg-bubble chat-msg-bubble-received">
                    Hello! I have reviewed your notes. Please stay hydrated and feel free to schedule a clinic consultation if symptoms persist.
                  </div>
                  <span class="chat-msg-time">09:35 AM</span>
                </div>
              `}
            </div>

            <form class="chat-input-row" id="chatSubmitForm">
              <input type="text" id="chatInputMsg" class="chat-input-field" placeholder="Type message to ${currentDoc.fullName}..." required autocomplete="off">
              <button type="submit" class="btn btn-teal">
                <i class="fa-solid fa-paper-plane"></i>
              </button>
            </form>
          </div>
        </div>
      </div>
    `;

    // Mobile Back to Doctor List Button
    document.getElementById("chatBackToListBtn")?.addEventListener("click", () => {
      this.mobileChatOpen = false;
      document.getElementById("chatLayoutGrid")?.classList.remove("show-conversation");
    });

    // Doctor Item Click
    container.querySelectorAll(".chat-doctor-item").forEach(item => {
      item.addEventListener("click", () => {
        this.mobileChatOpen = true;
        this.renderChat(container, parseInt(item.dataset.docuserid));
      });
    });

    // Chat Form Submit
    document.getElementById("chatSubmitForm")?.addEventListener("submit", (e) => {
      e.preventDefault();
      const input = document.getElementById("chatInputMsg");
      const text = input?.value.trim();
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
      this.mobileChatOpen = true;
      this.renderChat(container, currentDoc.id);

      const hist = document.getElementById("chatHistoryBox");
      if (hist) hist.scrollTop = hist.scrollHeight;
    });

    // Scroll to bottom
    const hist = document.getElementById("chatHistoryBox");
    if (hist) hist.scrollTop = hist.scrollHeight;
  },

  // -------------------------------------------------------------
  // View 8: Prediction History
  // -------------------------------------------------------------
  renderPredictionHistory(container) {
    const user = this.currentUser || { patientProfile: { id: 1 } };
    const preds = DB.getPredictions().filter(p => p.patientId === (user.patientProfile?.id || 1));

    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Prediction History</h2>
        <p style="color:var(--text-secondary); margin-bottom:1.5rem;">Review all previous symptom evaluations and risk logs.</p>
        <div class="stat-box">
          ${preds.length > 0 ? preds.map(p => `
            <div style="padding:1rem 0; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px;">
              <div>
                <strong style="color:var(--teal-primary); font-size:1rem;">${p.predictedConditionName}</strong> (${p.matchScore}% Match)
                <div style="font-size:0.825rem; color:var(--text-muted); margin-top:3px;">
                  Symptoms: ${(p.symptomsReported || []).join(', ')}
                </div>
              </div>
              <span class="badge-match">${new Date(p.createdAt).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}</span>
            </div>
          `).join('') : `
            <div style="text-align:center; padding:2rem; color:var(--text-muted);">
              No past symptom assessments found.
            </div>
          `}
        </div>
      </div>
    `;
  },

  // -------------------------------------------------------------
  // View 9: Patient Appointments
  // -------------------------------------------------------------
  renderAppointments(container) {
    const appts = DB.getAppointments();
    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">My Consultation Appointments</h2>
        <p style="color:var(--text-secondary); margin-bottom:1.25rem;">Track scheduled clinic consultations with Chennai physicians.</p>
        <div class="stat-box">
          ${appts.length > 0 ? appts.map(a => `
            <div style="padding:1rem 0; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px;">
              <div>
                <strong style="font-size:1rem; color:var(--text-primary);">${a.doctorName}</strong> 
                <span style="font-size:0.8rem; color:var(--text-muted);">(${a.doctorSpec})</span>
                <div style="font-size:0.85rem; color:var(--text-secondary); margin-top:4px;">
                  <i class="fa-solid fa-calendar"></i> ${a.appointmentDate} at ${a.appointmentTime} &nbsp;|&nbsp; <em>${a.notes}</em>
                </div>
              </div>
              <span class="badge-match">${a.status}</span>
            </div>
          `).join('') : `
            <div style="text-align:center; padding:2rem; color:var(--text-muted);">
              No active appointments found.
            </div>
          `}
        </div>
      </div>
    `;
  },

  // -------------------------------------------------------------
  // View 10: Doctor Advice
  // -------------------------------------------------------------
  renderDoctorAdvice(container) {
    const recs = DB.getRecommendations();
    container.innerHTML = `
      <div>
        <h2 class="predictor-header-title">Doctor Care Advice & Recommendations</h2>
        <p style="color:var(--text-secondary); margin-bottom:1.25rem;">Personalized clinical guidance and lifestyle instructions from attending physicians.</p>
        <div class="stat-box">
          ${recs.length > 0 ? recs.map(r => `
            <div style="padding:1.2rem 0; border-bottom:1px solid var(--border-subtle);">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px; flex-wrap:wrap;">
                <h3 style="font-size:1.05rem; font-weight:800; color:var(--teal-primary);">${r.doctorName} (${r.doctorSpec})</h3>
                <span class="badge-match">Follow-up: ${r.followUpDate}</span>
              </div>
              <p style="font-size:0.9rem; color:var(--text-primary); margin:6px 0;"><strong>Clinical Advice:</strong> ${r.clinicalAdvice}</p>
              <p style="font-size:0.85rem; color:var(--text-secondary);"><strong>Dietary / Lifestyle:</strong> ${r.dietaryLifestyle}</p>
            </div>
          `).join('') : `
            <div style="text-align:center; padding:2rem 0; color:var(--text-muted);">
              No personalized doctor recommendations received yet.
            </div>
          `}
        </div>
      </div>
    `;
  },

  // -------------------------------------------------------------
  // View 11: Profile
  // -------------------------------------------------------------
  renderProfile(container) {
    const u = this.currentUser || {};
    const isPatient = u.role === "PATIENT";
    const p = isPatient ? (u.patientProfile || {}) : (u.doctorProfile || {});

    container.innerHTML = `
      <div style="max-width: 650px;">
        <h2 class="predictor-header-title">Account & Medical Profile</h2>
        <p style="color:var(--text-secondary); margin-bottom:1.25rem;">Review and manage your credentials and health parameters.</p>
        <div class="stat-box">
          <div class="form-group">
            <label>Full Name</label>
            <input type="text" id="profFullName" class="form-input" value="${u.fullName || ''}">
          </div>
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" class="form-input" value="${u.email || ''}" disabled>
          </div>
          <div class="form-group">
            <label>Phone Number</label>
            <input type="tel" id="profPhone" class="form-input" value="${u.phone || ''}">
          </div>
          ${isPatient ? `
            <div class="form-row-2col">
              <div class="form-group">
                <label>Blood Group</label>
                <input type="text" class="form-input" value="${p.bloodGroup || 'O+'}" disabled>
              </div>
              <div class="form-group">
                <label>Gender</label>
                <input type="text" class="form-input" value="${p.gender || 'Not specified'}" disabled>
              </div>
            </div>
            <div class="form-group">
              <label>Known Medical Conditions / Allergies</label>
              <input type="text" id="profHistory" class="form-input" value="${p.medicalHistory || 'None'}">
            </div>
          ` : `
            <div class="form-group">
              <label>Specialization</label>
              <input type="text" class="form-input" value="${p.specialization || ''}" disabled>
            </div>
            <div class="form-group">
              <label>Hospital Affiliation</label>
              <input type="text" class="form-input" value="${p.hospital || ''}" disabled>
            </div>
          `}
          <button class="btn btn-teal" id="updateProfileBtn" style="margin-top:8px;">
            <i class="fa-solid fa-floppy-disk"></i> Save Profile Changes
          </button>
        </div>
      </div>
    `;

    document.getElementById("updateProfileBtn")?.addEventListener("click", () => {
      const name = document.getElementById("profFullName")?.value.trim();
      const phone = document.getElementById("profPhone")?.value.trim();
      if (name && this.currentUser) {
        this.currentUser.fullName = name;
        this.currentUser.phone = phone;
        this.setSession(this.currentUser);
        this.showToast("Profile updated successfully!", "success");
      }
    });
  },

  // -------------------------------------------------------------
  // View 12: Doctor Dashboard & Records
  // -------------------------------------------------------------
  renderDoctorDashboard(container) {
    const u = this.currentUser || {};
    const appts = DB.getAppointments();
    const patients = DB.getPatients();

    container.innerHTML = `
      <div class="welcome-header-card">
        <div>
          <h1 class="welcome-title">Physician Clinical Portal</h1>
          <p class="welcome-sub">${u.fullName} • ${u.doctorProfile?.specialization || 'Physician'} (${u.doctorProfile?.hospital || 'Kauvery Hospital, Chennai'})</p>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-box">
          <div class="stat-box-label">TODAY'S APPOINTMENTS</div>
          <div class="stat-box-value val-teal">${appts.length}</div>
          <div class="stat-box-desc">Scheduled consultations</div>
        </div>
        <div class="stat-box">
          <div class="stat-box-label">TOTAL PATIENTS</div>
          <div class="stat-box-value val-blue">${patients.length}</div>
          <div class="stat-box-desc">Active clinical records</div>
        </div>
        <div class="stat-box">
          <div class="stat-box-label">CONSULTATION FEE</div>
          <div class="stat-box-value val-cyan">₹${u.doctorProfile?.consultationFee || 600}</div>
          <div class="stat-box-desc">Standard session</div>
        </div>
        <div class="stat-box">
          <div class="stat-box-label">HOSPITAL CODE</div>
          <div class="stat-box-value val-purple" style="font-size:1.4rem;">CHN-MED</div>
          <div class="stat-box-desc">Accredited practice</div>
        </div>
      </div>

      <div class="stat-box">
        <h3 style="font-size:1.1rem; font-weight:800; color:var(--teal-primary); margin-bottom:1rem;">
          <i class="fa-solid fa-list-check"></i> Patient Consultation Queue
        </h3>
        ${appts.map(a => `
          <div style="padding:0.85rem 0; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px;">
            <div>
              <strong>${a.patientName}</strong> – ${a.appointmentDate} (${a.appointmentTime})
              <div style="font-size:0.825rem; color:var(--text-secondary); margin-top:2px;">Reason: ${a.notes}</div>
            </div>
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
        <p style="color:var(--text-secondary); margin-bottom:1.25rem;">Medical summaries and history of registered Chennai patients.</p>
        <div class="stat-box">
          ${patients.map(p => `
            <div style="padding:1rem 0; border-bottom:1px solid var(--border-subtle);">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px; flex-wrap:wrap;">
                <strong style="color:var(--teal-primary); font-size:1rem;">${p.fullName}</strong>
                <span class="badge-match">Blood: ${p.patientProfile?.bloodGroup || 'O+'}</span>
              </div>
              <div style="font-size:0.85rem; color:var(--text-secondary);">
                Gender: ${p.patientProfile?.gender || 'Patient'} &nbsp;|&nbsp; DOB: ${p.patientProfile?.dob || '1990-01-01'} &nbsp;|&nbsp; Phone: ${p.phone}
              </div>
              <div style="font-size:0.825rem; color:var(--text-muted); margin-top:4px;">
                Medical History: ${p.patientProfile?.medicalHistory || 'No major history recorded.'}
              </div>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  },

  // -------------------------------------------------------------
  // Modals & Booking Handling
  // -------------------------------------------------------------
  openModal(docId = null) {
    const select = document.getElementById("bookDoctorSelect");
    const doctors = DB.getDoctors();
    if (select) {
      select.innerHTML = doctors.map(d => `<option value="${d.doctorProfile?.id || d.id}" ${d.doctorProfile?.id === parseInt(docId) ? 'selected' : ''}>${d.fullName} – ${d.doctorProfile?.specialization} (₹${d.doctorProfile?.consultationFee})</option>`).join('');
    }

    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateInput = document.getElementById("bookDateInput");
    if (dateInput) dateInput.value = tomorrow.toISOString().split("T")[0];

    document.getElementById("bookingModal")?.classList.add("active");
  },

  closeModal() {
    document.getElementById("bookingModal")?.classList.remove("active");
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
    const icon = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-circle-exclamation' : 'fa-circle-info');
    t.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${msg}</span>`;
    c.appendChild(t);
    setTimeout(() => {
      t.style.opacity = '0';
      t.style.transition = 'opacity 0.3s ease';
      setTimeout(() => t.remove(), 300);
    }, 3000);
  }
};

document.addEventListener("DOMContentLoaded", () => App.init());

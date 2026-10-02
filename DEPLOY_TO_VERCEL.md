# 🚀 MediPredict – Vercel Live Deployment Guide

Your Java MediPredict application has been **converted into a modern, responsive web application** located in the [`web/`](file:///c:/Users/Sarvesh/OneDrive/Desktop/java%20proj/web) directory and configured for instant Vercel deployment!

---

## ⚡ Option 1: Instant 60-Second Deploy via Vercel CLI (Recommended)

Open your terminal in the project directory and run:

```bash
cd "c:\Users\Sarvesh\OneDrive\Desktop\java proj\web"
npx vercel
```

### Quick Prompts to Answer:
1. **Set up and deploy?** &rarr; Press `y` (Yes)
2. **Which scope do you want to deploy to?** &rarr; Select your Vercel account / Press Enter
3. **Link to existing project?** &rarr; Press `N` (No)
4. **What’s your project’s name?** &rarr; Type `medipredict` (or press Enter)
5. **In which directory is your code located?** &rarr; Press Enter (`./`)
6. **Want to modify these settings?** &rarr; Press `N` (No)

🎉 **Vercel will output your live URL immediately:**
```
✅  Production: https://medipredict-yourname.vercel.app [copied to clipboard]
```

To deploy future updates directly to production:
```bash
npx vercel --prod
```

---

## 🌐 Option 2: 1-Click Git Deploy via Vercel Dashboard

1. Push this repository to your **GitHub** / **GitLab** / **Bitbucket** account.
2. Go to [vercel.com/new](https://vercel.com/new).
3. Click **Import** next to your `java proj` repository.
4. Set the **Root Directory** to `web` (or leave as default with included root [`vercel.json`](file:///c:/Users/Sarvesh/OneDrive/Desktop/java%20proj/vercel.json)).
5. Click **Deploy**.
6. Your live `.vercel.app` link will be active in seconds with automatic CI/CD on every git push!

---

## 💻 Option 3: Local Live Preview

You can run the web application locally anytime:

```bash
cd "c:\Users\Sarvesh\OneDrive\Desktop\java proj\web"
npx serve -p 3000
```
Open **[http://localhost:3000](http://localhost:3000)** in your browser.

---

## 🩺 Converted Application Features

| Feature | Details |
|---|---|
| **AI Symptom Analyzer** | 27 categorized symptoms, instant tag selection, live match computation. |
| **Prediction Engine** | Exact multi-factor $CMI$ algorithm, Confidence scoring, Risk assessment & Differentials. |
| **Doctor Directory** | Search by specialty (Neurology, Pulmonology, Cardiology, Gastroenterology, General), fees, hospital. |
| **Appointment Booking** | Collision detection, scheduling modal, doctor status workflow (Pending &rarr; Confirmed &rarr; Completed). |
| **Patient EHR & Care Plans** | Prescription advice, lifestyle guidelines, follow-up dates, medical report printing. |
| **Consultation Chat** | Real-time messaging between patients and physicians. |
| **Quick Demo Switcher** | 1-click login for Sarvesh Kumar, Emma Watson, Dr. Robert Chen, Dr. Emily Watson, and more. |
| **Themes** | Modern Glassmorphism with Dark & Clinical Light modes. |

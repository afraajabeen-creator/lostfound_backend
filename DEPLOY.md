# 🚀 Backend Deployment Guide (Step-by-Step for Beginners)

This guide walks you through deploying your Spring Boot backend to the cloud for free using **Render** and a free cloud MySQL database (such as **Aiven** or **TiDB Cloud**).

---

## 🗄️ Step 1: Set Up Free Cloud MySQL Database

Render does not offer a free MySQL tier, so we use a free cloud MySQL provider:

### Option A: Using Aiven (Recommended & Easiest)
1. Go to [https://aiven.io](https://aiven.io) and sign up for a free account (no credit card required).
2. Click **Create Service**.
3. Select **MySQL** as the service type and choose the **Free Plan**.
4. Select a cloud region closest to you (e.g., AWS Frankfurt, Singapore, or US).
5. Once your service status is **Running** (takes 1-2 minutes):
   - Under **Connection Details**, locate:
     - **Host**: e.g., `mysql-1234.aivencloud.com`
     - **Port**: e.g., `12345`
     - **User**: `avnadmin`
     - **Password**: *(click 'Show' to copy)*
     - **Database name**: `defaultdb` (or create a database named `lostfound` in the Databases tab)
6. Your Spring Boot JDBC URL will look like:
   ```
   jdbc:mysql://<HOST>:<PORT>/<DATABASE_NAME>?sslMode=REQUIRED
   ```

### Option B: Using TiDB Cloud (Serverless MySQL)
1. Go to [https://tidbcloud.com](https://tidbcloud.com) and create a free account.
2. Create a free **Serverless Cluster**.
3. Under **Connect**, choose **General / Java (JDBC)**.
4. Copy the JDBC connection string, username, and password.

---

## ☁️ Step 2: Deploy Spring Boot Backend to Render

1. Go to [https://render.com](https://render.com) and sign in (you can sign in with your GitHub account).
2. Click **New +** in the top navigation bar and select **Web Service**.
3. Choose **Build and deploy from a Git repository** and select your `lostfound-backend` repository.
4. Configure the service settings:
   - **Name**: `lostfound-backend` (or your choice)
   - **Region**: Choose the region closest to your cloud database
   - **Branch**: `main`
   - **Runtime**: Select **Docker** *(Render will automatically detect the `Dockerfile` in the root of the project!)*
   - **Instance Type**: **Free** (0.1 CPU, 512 MB RAM)

5. Scroll down to **Environment Variables** and add the following keys and values:

   | Key | Value | Notes |
   | :--- | :--- | :--- |
   | `PORT` | `8080` | Render port configuration |
   | `DB_URL` | `jdbc:mysql://<HOST>:<PORT>/<DATABASE_NAME>?sslMode=REQUIRED` | Copied from Step 1 |
   | `DB_USER` | `avnadmin` (or your cloud DB user) | Cloud DB username |
   | `DB_PASS` | `<YOUR_CLOUD_DB_PASSWORD>` | Cloud DB password |
   | `JWT_SECRET` | `CampusLostAndFoundSecretKey2026SecureMustBe32BytesLongForHS256!` | Secure 32+ character key |
   | `CORS_ORIGINS` | `http://localhost:5173,https://<your-frontend-app>.vercel.app` | Include your frontend URL |

6. Click **Create Web Service**.
7. Render will build the Docker container and start your Spring Boot application.
8. Once the build logs display `Build successful` and `Live`, copy your public Render service URL:
   👉 E.g.: `https://lostfound-backend-xyz.onrender.com`

---

## 🔄 Step 3: Link Backend with Your Frontend
1. Copy the Render URL from Step 2.
2. In your React frontend settings (on Vercel or Netlify), set the environment variable:
   ```
   VITE_API_URL=https://lostfound-backend-xyz.onrender.com
   ```
3. In Render, ensure your `CORS_ORIGINS` environment variable includes your frontend domain (e.g., `https://your-app.vercel.app`).

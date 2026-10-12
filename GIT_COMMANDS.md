# 📦 Git Commands for Backend Repository

Follow these exact steps to push this backend folder to its own GitHub repository.

---

## 1. Create an Empty Repository on GitHub First

1. Log into your GitHub account at [https://github.com](https://github.com).
2. Click the **+** icon in the top-right corner and choose **New repository**.
3. Set the Repository name to: `lostfound-backend`.
4. Choose **Public** (or Private).
5. ⚠️ **IMPORTANT**: Leave the following checkboxes **UNCHECKED**:
   - ❌ Do NOT check "Add a README file"
   - ❌ Do NOT check "Add .gitignore"
   - ❌ Do NOT check "Choose a license"
   *(This ensures you create a completely empty repository).*
6. Click **Create repository**.
7. Copy the repository URL (e.g., `https://github.com/<your-username>/lostfound-backend.git`).

---

## 2. Push Your Local Code from Terminal / CMD

Open Command Prompt (`cmd`) or PowerShell, navigate directly inside this backend directory:
```cmd
cd %USERPROFILE%\Desktop\tt\lostfound_backend
```

Run the following commands in order:

### 1. Initialize Git
```cmd
git init
```

### 2. Stage All Files
```cmd
git add .
```

### 3. Check Git Status (CRITICAL SECURITY CHECK)
```cmd
git status
```
> ⚠️ **IMPORTANT**: Look closely at the files listed under "Changes to be committed".
> **Verify that `src/main/resources/application-local.properties` is NOT listed!**
> Because it is in `.gitignore`, it should remain hidden from Git so your database password is never exposed.

### 4. Create Initial Commit
```cmd
git commit -m "Initial lost and found backend"
```

### 5. Rename Default Branch to main
```cmd
git branch -M main
```

### 6. Link Remote GitHub Repository
*(Replace `<your-username>` with your actual GitHub username)*
```cmd
git remote add origin https://github.com/<your-username>/lostfound-backend.git
```

### 7. Push to GitHub
```cmd
git push -u origin main
```

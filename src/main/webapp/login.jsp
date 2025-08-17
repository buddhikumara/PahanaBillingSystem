<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Pahana Billing System — Login</title>
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

  <style>
    :root{ --bg1: #04394e; --bg2: #035954; --bg3: #52a8ef; --border:rgba(255,255,255,.25); }
    body{
      min-height:100vh; color:#eef2ff;
      background:linear-gradient(120deg,var(--bg1),var(--bg2),var(--bg3));
      background-size:200% 200%; animation:bgShift 12s ease-in-out infinite; position:relative;
    }
    @keyframes bgShift{0%{background-position:0% 50%}50%{background-position:100% 50%}100%{background-position:0% 50%}}
    body::after{content:"";position:fixed;inset:0;opacity:.4;pointer-events:none;mix-blend-mode:soft-light;
      background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120' viewBox='0 0 120 120'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3CfeColorMatrix type='saturate' values='0'/%3E%3CfeComponentTransfer%3E%3CfeFuncA type='table' tableValues='0 0 0 .03 .08 .03 0 0 0'/%3E%3C/feComponentTransfer%3E%3CfeBlend mode='overlay'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
    }
    .wrap{min-height:100vh;display:grid;place-items:center;padding:32px 16px;}
    .brand{
      font-weight:800;line-height:1.05;letter-spacing:.5px;margin-bottom:10px;
      font-size:clamp(1.75rem,2.5vw + 1rem,3rem);
      background:linear-gradient(90deg,#fff,#e9d5ff, #b15d5d,#fff);background-size:300% 100%;
      -webkit-background-clip:text;background-clip:text;color:transparent;animation:shimmer 4.5s linear infinite;
      text-shadow:0 1px 10px rgba(0,0,0,.15);
    }
    @keyframes shimmer{0%{background-position:0% 50%}100%{background-position:300% 50%}}
    .subtitle{color:#e2e8f0;opacity:.9;font-weight:500;text-shadow:0 1px 6px rgba(0,0,0,.2);}
    .card-glass{
      width:min(960px,100%);border-radius:24px;border:1px solid var(--border);
      background:linear-gradient(180deg,rgba(255,255,255,.20),rgba(255,255,255,.08));
      box-shadow:0 20px 60px rgba(0,0,0,.35), inset 0 1px rgba(255,255,255,.25);
      backdrop-filter:blur(10px);overflow:hidden;
    }
    .card-left{position:relative;padding:32px;border-right:1px solid var(--border);}
    .badge-soft{display:inline-block;padding:.35rem .65rem;border-radius:999px;background:rgba(255,255,255,.18);border:1px solid rgba(255,255,255,.3);color:#fff;font-size:.82rem;}
    .features li{margin-bottom:.35rem;}
    .card-right{padding:32px;background:rgba(255,255,255,.05);}
    .form-control,.form-check-input{background:rgba(255,255,255,.12);color:#fff;border:1px solid rgba(255,255,255,.35);}
    .form-control::placeholder{color:rgba(255,255,255,.75);}
    .form-control:focus{background:rgba(255,255,255,.18);box-shadow:0 0 0 .25rem rgba(255,255,255,.15);color:#fff;}
    .btn-primary{background:linear-gradient(90deg,#22c55e,#16a34a);border:none;font-weight:600;box-shadow:0 10px 20px rgba(34,197,94,.35);}
    .btn-primary:hover{filter:brightness(1.07);}
    .muted-link a{color:#e2e8f0;text-decoration:none;} .muted-link a:hover{text-decoration:underline;}
    .toast-container{z-index:1100;}
    .shake{animation:shake .35s ease-in-out;}
    @keyframes shake{
      10%,90%{transform:translateX(-1px)}
      20%,80%{transform:translateX(2px)}
      30%,50%,70%{transform:translateX(-4px)}
      40%,60%{transform:translateX(4px)}
    }
    @media (prefers-reduced-motion: reduce){*{animation:none !important;transition:none !important}}
  </style>
</head>
<body>
<%
  // Move flash from session -> request so JSP can read it
  String _err = (String) session.getAttribute("flashError");
  if (_err != null) { request.setAttribute("flashError", _err); session.removeAttribute("flashError"); }
%>

<!-- Toasts (top-right) -->
<div class="toast-container position-fixed top-0 end-0 p-3">
  <c:if test="${not empty errorMessage}">
    <div class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true" id="toastErrorDirect">
      <div class="d-flex">
        <div class="toast-body">
          <c:out value="${errorMessage}"/>
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </c:if>

  <c:if test="${not empty flashError}">
    <div class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true" id="toastErrorFlash">
      <div class="d-flex">
        <div class="toast-body">
          <c:out value="${flashError}"/>
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </c:if>

  <c:if test="${param.msg == 'loggedout'}">
    <div class="toast align-items-center text-bg-success border-0" role="alert" aria-live="polite" aria-atomic="true" id="toastLogout">
      <div class="d-flex">
        <div class="toast-body">You have been logged out.</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </c:if>

  <c:if test="${param.msg == 'reset_ok'}">
    <div class="toast align-items-center text-bg-success border-0" role="alert" aria-live="polite" aria-atomic="true" id="toastReset">
      <div class="d-flex">
        <div class="toast-body">Password updated. Please log in.</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </c:if>
</div>

<div class="wrap">
  <div class="text-center mb-3">
    <div class="brand">Pahana Billing System</div>
    <div class="subtitle">Fast • Simple • Reliable</div>
  </div>

  <div class="card-glass row g-0" id="loginCard">
    <!-- Left: highlights -->
    <div class="col-md-6 card-left">
      <span class="badge-soft mb-3">Welcome</span>
      <h4 class="mb-2">Sign in to continue</h4>
      <p class="mb-4">Manage customers, items, billing, and reports in one place.</p>
      <ul class="features list-unstyled small">
        <li>✓ Quick billing with keyboard shortcuts</li>
        <li>✓ Stock warnings & invoice printing</li>
        <li>✓ Daily, item-wise, and customer reports</li>
        <li>✓ Role-based access & audit-friendly design</li>
      </ul>
      <div class="mt-4 muted-link">
        <a href="${pageContext.request.contextPath}/help">Need help?</a>
      </div>
    </div>

    <!-- Right: form -->
    <div class="col-md-6 card-right">
      <!-- ✅ Fixed: post to servlet (/login), not the JSP -->
      <form method="post" action="${pageContext.request.contextPath}/login" id="loginForm" novalidate>
        <div class="mb-3">
          <label class="form-label">Username</label>
          <input type="text" class="form-control form-control-lg" name="username"
                 placeholder="Enter username" required>
          <div class="invalid-feedback">Username is required.</div>
        </div>

        <div class="mb-2 position-relative">
          <label class="form-label">Password</label>
          <div class="input-group input-group-lg">
            <input type="password" class="form-control" id="password" name="password"
                   placeholder="Enter password" minlength="4" required>
            <button class="btn btn-outline-light" type="button" id="togglePwd" title="Show/Hide">
              <span id="eye">👁️</span>
            </button>
          </div>
          <div class="invalid-feedback">Password is required (min 4 chars).</div>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" value="1" id="remember" name="remember">
            <label class="form-check-label" for="remember">Remember me</label>
          </div>
          <a class="small text-decoration-none" href="${pageContext.request.contextPath}/forgot-password">Forgot password?</a>
        </div>

        <button class="btn btn-primary btn-lg w-100" type="submit">Login</button>
      </form>

      <div class="text-center mt-3 small muted-link">
        <span>© <script>document.write(new Date().getFullYear())</script> Pahana</span>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Show all toasts on load (auto-hide after 4s)
  document.addEventListener("DOMContentLoaded", function () {
    const toastEls = document.querySelectorAll('.toast');
    toastEls.forEach(function (el) {
      const t = new bootstrap.Toast(el, { delay: 4000 });
      t.show();
    });

    // If there was an error toast, shake the card once
    if (document.getElementById('toastErrorDirect') || document.getElementById('toastErrorFlash')) {
      const card = document.getElementById('loginCard');
      card.classList.add('shake');
      setTimeout(()=>card.classList.remove('shake'), 600);
    }
  });

  // Client-side validation + double-submit guard
  (function(){
    const form = document.getElementById('loginForm');
    form.addEventListener('submit', function(e){
      if(!form.checkValidity()){
        e.preventDefault();
        e.stopPropagation();
      }
      form.classList.add('was-validated');
      if(form.checkValidity()){
        const btn = form.querySelector('button[type="submit"]');
        btn.disabled = true; btn.textContent = 'Signing in...';
      }
    });
  })();

  // Toggle password visibility
  document.getElementById('togglePwd').addEventListener('click', function(){
    const pwd = document.getElementById('password');
    const eye = document.getElementById('eye');
    const type = pwd.getAttribute('type') === 'password' ? 'text' : 'password';
    pwd.setAttribute('type', type);
    eye.textContent = type === 'password' ? '👁️' : '🙈';
  });
</script>
</body>
</html>

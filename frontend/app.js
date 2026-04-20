const state = {
    token: localStorage.getItem("threadline_token") || "",
    email: localStorage.getItem("threadline_email") || "",
    role: localStorage.getItem("threadline_role") || "Guest",
    apiBaseUrl: localStorage.getItem("threadline_api_base") || window.location.origin,
    authView: "login",
    products: [],
    paints: [],
    printJobs: [],
    stockHistory: []
};

const refs = {
    authGate: document.getElementById("authGate"),
    appShell: document.getElementById("appShell"),
    showLoginBtn: document.getElementById("showLoginBtn"),
    showRegisterBtn: document.getElementById("showRegisterBtn"),
    loginForm: document.getElementById("loginForm"),
    registerForm: document.getElementById("registerForm"),
    navLinks: [...document.querySelectorAll(".nav-link")],
    sections: [...document.querySelectorAll(".panel-section")],
    logoutBtn: document.getElementById("logoutBtn"),
    authState: document.getElementById("authState"),
    loginEmail: document.getElementById("loginEmail"),
    loginPassword: document.getElementById("loginPassword"),
    loginApiBaseUrl: document.getElementById("loginApiBaseUrl"),
    registerFullName: document.getElementById("registerFullName"),
    registerEmail: document.getElementById("registerEmail"),
    registerPassword: document.getElementById("registerPassword"),
    registerApiBaseUrl: document.getElementById("registerApiBaseUrl"),
    refreshAllBtn: document.getElementById("refreshAllBtn"),
    totalStockValue: document.getElementById("totalStockValue"),
    totalStockCost: document.getElementById("totalStockCost"),
    estimatedProfitPotential: document.getElementById("estimatedProfitPotential"),
    completedJobsRevenue: document.getElementById("completedJobsRevenue"),
    completedJobsProfit: document.getElementById("completedJobsProfit"),
    averageDailyProfit30Days: document.getElementById("averageDailyProfit30Days"),
    projectedWeeklyProfit: document.getElementById("projectedWeeklyProfit"),
    lowStockCount: document.getElementById("lowStockCount"),
    categoryCounts: document.getElementById("categoryCounts"),
    lowStockAlerts: document.getElementById("lowStockAlerts"),
    productsGrid: document.getElementById("productsGrid"),
    paintGrid: document.getElementById("paintGrid"),
    jobsGrid: document.getElementById("jobsGrid"),
    stockHistory: document.getElementById("stockHistory"),
    reportFrom: document.getElementById("reportFrom"),
    reportTo: document.getElementById("reportTo"),
    loadReportsBtn: document.getElementById("loadReportsBtn"),
    downloadStockCsvBtn: document.getElementById("downloadStockCsvBtn"),
    downloadPaintCsvBtn: document.getElementById("downloadPaintCsvBtn"),
    downloadJobsCsvBtn: document.getElementById("downloadJobsCsvBtn"),
    stockReportTitle: document.getElementById("stockReportTitle"),
    stockReportSummary: document.getElementById("stockReportSummary"),
    stockReportItems: document.getElementById("stockReportItems"),
    jobsReportTitle: document.getElementById("jobsReportTitle"),
    jobsReportSummary: document.getElementById("jobsReportSummary"),
    jobsReportItems: document.getElementById("jobsReportItems"),
    productForm: document.getElementById("productForm"),
    stockForm: document.getElementById("stockForm"),
    paintForm: document.getElementById("paintForm"),
    printJobForm: document.getElementById("printJobForm"),
    productSku: document.getElementById("productSku"),
    productName: document.getElementById("productName"),
    productCategory: document.getElementById("productCategory"),
    productDescription: document.getElementById("productDescription"),
    variantSize: document.getElementById("variantSize"),
    variantColor: document.getElementById("variantColor"),
    variantQuantity: document.getElementById("variantQuantity"),
    variantUnitCost: document.getElementById("variantUnitCost"),
    variantRetailPrice: document.getElementById("variantRetailPrice"),
    variantThreshold: document.getElementById("variantThreshold"),
    variantBarcode: document.getElementById("variantBarcode"),
    stockVariantId: document.getElementById("stockVariantId"),
    stockAction: document.getElementById("stockAction"),
    stockQuantity: document.getElementById("stockQuantity"),
    stockUnitCost: document.getElementById("stockUnitCost"),
    stockReference: document.getElementById("stockReference"),
    stockNote: document.getElementById("stockNote"),
    paintName: document.getElementById("paintName"),
    paintType: document.getElementById("paintType"),
    paintColor: document.getElementById("paintColor"),
    paintQuantity: document.getElementById("paintQuantity"),
    paintUnit: document.getElementById("paintUnit"),
    paintCost: document.getElementById("paintCost"),
    paintThreshold: document.getElementById("paintThreshold"),
    printJobProductId: document.getElementById("printJobProductId"),
    printJobVariantId: document.getElementById("printJobVariantId"),
    printJobQuantity: document.getElementById("printJobQuantity"),
    printJobStatus: document.getElementById("printJobStatus"),
    printJobPaintId: document.getElementById("printJobPaintId"),
    printJobPaintQuantity: document.getElementById("printJobPaintQuantity"),
    printJobNotes: document.getElementById("printJobNotes"),
    settingsApiUrl: document.getElementById("settingsApiUrl"),
    settingsSessionState: document.getElementById("settingsSessionState"),
    resetForm: document.getElementById("resetForm"),
    resetPassword: document.getElementById("resetPassword"),
    toastHost: document.getElementById("toastHost")
};

function init() {
    setDefaultDates();
    bindEvents();
    hydrateAuthUi();
    if (state.token) {
        setShellVisibility(true);
        loadAll();
    } else {
        activateAuthView("login");
        setShellVisibility(false);
    }
}

function bindEvents() {
    refs.navLinks.forEach((button) => {
        button.addEventListener("click", () => activatePanel(button.dataset.target));
    });

    refs.showLoginBtn.addEventListener("click", () => activateAuthView("login"));
    refs.showRegisterBtn.addEventListener("click", () => activateAuthView("register"));
    refs.loginForm.addEventListener("submit", handleLogin);
    refs.registerForm.addEventListener("submit", handleRegister);
    refs.logoutBtn.addEventListener("click", logout);
    refs.refreshAllBtn.addEventListener("click", loadAll);
    refs.loadReportsBtn.addEventListener("click", loadReports);
    refs.downloadStockCsvBtn.addEventListener("click", () => downloadReport("/api/reports/stock/export/csv", "stock-report.csv"));
    refs.downloadPaintCsvBtn.addEventListener("click", () => downloadReport("/api/reports/paint-usage/export/csv", "paint-usage-report.csv"));
    refs.downloadJobsCsvBtn.addEventListener("click", () => downloadReport("/api/reports/print-jobs/export/csv", "print-jobs-report.csv"));
    refs.loginApiBaseUrl.addEventListener("change", syncApiBaseUrlFromInputs);
    refs.registerApiBaseUrl.addEventListener("change", syncApiBaseUrlFromInputs);

    refs.productForm.addEventListener("submit", createProduct);
    refs.stockForm.addEventListener("submit", submitStockAction);
    refs.paintForm.addEventListener("submit", createPaint);
    refs.printJobForm.addEventListener("submit", createPrintJob);
    refs.resetForm.addEventListener("submit", resetInventory);
    refs.printJobProductId.addEventListener("change", syncPrintJobVariantOptions);
}

function activatePanel(target) {
    refs.navLinks.forEach((link) => link.classList.toggle("active", link.dataset.target === target));
    refs.sections.forEach((section) => section.classList.toggle("active", section.id === target));
}

async function handleLogin(event) {
    event.preventDefault();
    state.apiBaseUrl = cleanBaseUrl(refs.loginApiBaseUrl.value);

    try {
        const payload = await request("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                email: refs.loginEmail.value.trim(),
                password: refs.loginPassword.value
            })
        }, false);

        applyAuthSuccess(payload, "Signed in", `Authenticated as ${payload.email}.`);
        await loadAll();
    } catch (error) {
        showToast("Login failed", normalizeErrorMessage(error.message), "error");
    }
}

async function handleRegister(event) {
    event.preventDefault();
    state.apiBaseUrl = cleanBaseUrl(refs.registerApiBaseUrl.value);

    try {
        const payload = await request("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                fullName: refs.registerFullName.value.trim(),
                email: refs.registerEmail.value.trim(),
                password: refs.registerPassword.value
            })
        }, false);

        refs.registerForm.reset();
        refs.registerApiBaseUrl.value = state.apiBaseUrl;
        applyAuthSuccess(payload, "Account created", `Welcome ${payload.email}. Your workspace is ready.`);
        await loadAll();
    } catch (error) {
        showToast("Registration failed", normalizeErrorMessage(error.message), "error");
    }
}

function logout() {
    state.token = "";
    state.email = "";
    state.role = "Guest";
    state.products = [];
    state.paints = [];
    state.printJobs = [];
    state.stockHistory = [];
    persistSession();
    hydrateAuthUi();
    clearWorkspace();
    activateAuthView("login");
    setShellVisibility(false);
    showToast("Signed out", "Your workspace session has been closed.", "success");
}

function hydrateAuthUi() {
    refs.loginApiBaseUrl.value = state.apiBaseUrl;
    refs.registerApiBaseUrl.value = state.apiBaseUrl;
    refs.authState.textContent = state.token ? "Signed in" : "Signed out";
    refs.settingsApiUrl.textContent = state.apiBaseUrl;
    refs.settingsSessionState.textContent = state.token
        ? `Authenticated as ${state.email} (${state.role.replace("ROLE_", "")})`
        : "Signed out";
}

function persistSession() {
    localStorage.setItem("threadline_api_base", state.apiBaseUrl);
    if (state.token) {
        localStorage.setItem("threadline_token", state.token);
        localStorage.setItem("threadline_email", state.email);
        localStorage.setItem("threadline_role", state.role);
    } else {
        localStorage.removeItem("threadline_token");
        localStorage.removeItem("threadline_email");
        localStorage.removeItem("threadline_role");
    }
}

async function loadAll() {
    if (!state.token) {
        return;
    }

    try {
        const [summaryResult, productsResult, paintsResult, jobsResult, stockHistoryResult] = await Promise.allSettled([
            apiGet("/api/products/dashboard/summary"),
            apiGet("/api/products?page=0&size=30"),
            apiGet("/api/paints"),
            apiGet("/api/print-jobs?page=0&size=12"),
            apiGet("/api/stock/history?page=0&size=8")
        ]);

        const summary = settledValue(summaryResult, {
            totalStockValue: 0,
            totalStockCost: 0,
            estimatedProfitPotential: 0,
            completedJobsRevenue: 0,
            completedJobsProfit: 0,
            averageDailyProfit30Days: 0,
            projectedWeeklyProfit: 0,
            categoryCounts: [],
            lowStockAlerts: []
        }, "dashboard summary");
        const products = settledValue(productsResult, { content: [] }, "products");
        const paints = settledValue(paintsResult, [], "paints");
        const jobs = settledValue(jobsResult, { content: [] }, "print jobs");
        const stockHistory = settledValue(stockHistoryResult, { content: [] }, "stock history");

        state.products = products.content || [];
        state.paints = paints || [];
        state.printJobs = jobs.content || [];
        state.stockHistory = stockHistory.content || [];

        renderSummary(summary);
        renderProducts(state.products);
        renderPaints(state.paints);
        renderJobs(state.printJobs);
        renderStockHistory(state.stockHistory);
        populateSelectors();
        await loadReports();
    } catch (error) {
        showToast("Sync failed", normalizeErrorMessage(error.message), "error");
    }
}

function activateAuthView(view) {
    state.authView = view;
    refs.showLoginBtn.classList.toggle("active", view === "login");
    refs.showRegisterBtn.classList.toggle("active", view === "register");
    refs.loginForm.classList.toggle("hidden", view !== "login");
    refs.registerForm.classList.toggle("hidden", view !== "register");
}

function setShellVisibility(isAuthenticated) {
    refs.authGate.classList.toggle("hidden", isAuthenticated);
    refs.appShell.classList.toggle("hidden", !isAuthenticated);
}

function applyAuthSuccess(payload, title, message) {
    state.token = payload.token;
    state.email = payload.email;
    state.role = payload.role;
    persistSession();
    hydrateAuthUi();
    setShellVisibility(true);
    activatePanel("overview");
    showToast(title, message, "success");
}

function syncApiBaseUrlFromInputs(event) {
    state.apiBaseUrl = cleanBaseUrl(event.target.value);
    persistSession();
    hydrateAuthUi();
}

async function createProduct(event) {
    event.preventDefault();
    if (!ensureSignedIn()) {
        return;
    }

    const payload = {
        sku: refs.productSku.value.trim(),
        name: refs.productName.value.trim(),
        category: refs.productCategory.value,
        description: refs.productDescription.value.trim(),
        active: true,
        variants: [{
            size: refs.variantSize.value,
            color: refs.variantColor.value.trim(),
            quantityInStock: toInt(refs.variantQuantity.value),
            unitCost: toNumber(refs.variantUnitCost.value),
            retailPrice: toNumber(refs.variantRetailPrice.value),
            barcode: refs.variantBarcode.value.trim() || null,
            lowStockThreshold: toInt(refs.variantThreshold.value)
        }]
    };

    try {
        await apiPost("/api/products", payload);
        refs.productForm.reset();
        refs.productCategory.value = "PLAIN_T_SHIRTS";
        refs.variantSize.value = "M";
        refs.variantThreshold.value = "10";
        showToast("Product created", "The product and starter variant were saved successfully.", "success");
        await loadAll();
        activatePanel("products");
    } catch (error) {
        showToast("Product creation failed", normalizeErrorMessage(error.message), "error");
    }
}

async function submitStockAction(event) {
    event.preventDefault();
    if (!ensureSignedIn()) {
        return;
    }

    const action = refs.stockAction.value;
    const basePayload = {
        variantId: Number(refs.stockVariantId.value),
        reference: refs.stockReference.value.trim(),
        note: refs.stockNote.value.trim() || null
    };
    const unitCost = refs.stockUnitCost.value ? toNumber(refs.stockUnitCost.value) : null;

    try {
        if (action === "adjust") {
            await apiPut("/api/stock/adjust", {
                ...basePayload,
                newQuantity: toInt(refs.stockQuantity.value),
                unitCost
            });
        } else {
            await apiPost(`/api/stock/${action}`, {
                ...basePayload,
                quantity: toInt(refs.stockQuantity.value),
                unitCost
            });
        }
        refs.stockForm.reset();
        refs.stockAction.value = "add";
        refs.stockQuantity.value = "1";
        showToast("Stock updated", "The stock action was recorded successfully.", "success");
        await loadAll();
    } catch (error) {
        showToast("Stock update failed", normalizeErrorMessage(error.message), "error");
    }
}

async function createPaint(event) {
    event.preventDefault();
    if (!ensureSignedIn()) {
        return;
    }

    try {
        await apiPost("/api/paints", {
            name: refs.paintName.value.trim(),
            paintType: refs.paintType.value.trim(),
            color: refs.paintColor.value.trim(),
            quantityAvailable: toNumber(refs.paintQuantity.value),
            unit: refs.paintUnit.value,
            costPerUnit: toNumber(refs.paintCost.value),
            lowStockThreshold: toNumber(refs.paintThreshold.value),
            active: true
        });
        refs.paintForm.reset();
        refs.paintUnit.value = "LITERS";
        refs.paintThreshold.value = "5";
        showToast("Paint created", "The paint inventory line was saved successfully.", "success");
        await loadAll();
        activatePanel("paint");
    } catch (error) {
        showToast("Paint creation failed", normalizeErrorMessage(error.message), "error");
    }
}

async function createPrintJob(event) {
    event.preventDefault();
    if (!ensureSignedIn()) {
        return;
    }

    const variantValue = refs.printJobVariantId.value;
    try {
        await apiPost("/api/print-jobs", {
            productId: Number(refs.printJobProductId.value),
            variantId: variantValue ? Number(variantValue) : null,
            quantityPrinted: toInt(refs.printJobQuantity.value),
            status: refs.printJobStatus.value,
            notes: refs.printJobNotes.value.trim(),
            paintUsages: [{
                paintId: Number(refs.printJobPaintId.value),
                quantityUsed: toNumber(refs.printJobPaintQuantity.value)
            }]
        });
        refs.printJobForm.reset();
        refs.printJobStatus.value = "COMPLETED";
        refs.printJobPaintQuantity.value = "0.5";
        showToast("Print job created", "The print job and paint usage were saved successfully.", "success");
        await loadAll();
        activatePanel("jobs");
    } catch (error) {
        showToast("Print job failed", normalizeErrorMessage(error.message), "error");
    }
}

async function resetInventory(event) {
    event.preventDefault();
    if (!ensureSignedIn()) {
        return;
    }

    const password = refs.resetPassword.value;
    if (!password) {
        showToast("Password required", "Enter the admin password before resetting the inventory.", "error");
        return;
    }

    const confirmed = window.confirm("This will permanently clear products, stock movements, paints, and print jobs. Continue?");
    if (!confirmed) {
        return;
    }

    try {
        await apiPost("/api/admin/reset", { password });
        refs.resetForm.reset();
        showToast("Inventory reset", "All stock, paints, print jobs, and history have been cleared.", "success");
        await loadAll();
        activatePanel("overview");
    } catch (error) {
        showToast("Reset failed", normalizeErrorMessage(error.message), "error");
    }
}

async function loadReports() {
    if (!state.token) {
        return;
    }

    const from = refs.reportFrom.value;
    const to = refs.reportTo.value;
    try {
        const [stockReport, jobsReport] = await Promise.all([
            apiGet(`/api/reports/stock?from=${from}&to=${to}`),
            apiGet(`/api/reports/print-jobs?from=${from}&to=${to}`)
        ]);
        renderReport(stockReport, refs.stockReportTitle, refs.stockReportSummary, refs.stockReportItems, formatStockReportItem);
        renderReport(jobsReport, refs.jobsReportTitle, refs.jobsReportSummary, refs.jobsReportItems, formatJobsReportItem);
    } catch (error) {
        showToast("Reports unavailable", normalizeErrorMessage(error.message), "error");
    }
}

async function downloadReport(path, fileName) {
    if (!ensureSignedIn()) {
        return;
    }

    const from = refs.reportFrom.value;
    const to = refs.reportTo.value;
    try {
        const response = await fetch(`${state.apiBaseUrl}${path}?from=${from}&to=${to}`, {
            headers: { Authorization: `Bearer ${state.token}` }
        });
        if (!response.ok) {
            const errorPayload = await response.text();
            throw new Error(errorPayload || "Failed to download report.");
        }
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(url);
    } catch (error) {
        showToast("Download failed", normalizeErrorMessage(error.message), "error");
    }
}

async function apiGet(path) {
    return request(path, {
        headers: {
            Authorization: `Bearer ${state.token}`
        }
    });
}

async function apiPost(path, payload) {
    return request(path, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${state.token}`
        },
        body: JSON.stringify(payload)
    });
}

async function apiPut(path, payload) {
    return request(path, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${state.token}`
        },
        body: JSON.stringify(payload)
    });
}

async function request(path, options = {}, expectAuth = true) {
    const response = await fetch(`${state.apiBaseUrl}${path}`, options);
    const contentType = response.headers.get("content-type") || "";
    const payload = contentType.includes("application/json") ? await response.json() : await response.text();

    if (!response.ok) {
        const message = typeof payload === "string"
                ? payload
                : payload.message || "Request failed";
        if (response.status === 401 && expectAuth) {
            expireSession();
            throw new Error("Your session expired. Please sign in again.");
        }
        throw new Error(message);
    }
    return payload;
}

function renderSummary(summary) {
    refs.totalStockValue.textContent = formatMoney(summary.totalStockValue || 0);
    refs.totalStockCost.textContent = formatMoney(summary.totalStockCost || 0);
    refs.estimatedProfitPotential.textContent = formatMoney(summary.estimatedProfitPotential || 0);
    refs.completedJobsRevenue.textContent = formatMoney(summary.completedJobsRevenue || 0);
    refs.completedJobsProfit.textContent = formatMoney(summary.completedJobsProfit || 0);
    refs.averageDailyProfit30Days.textContent = formatMoney(summary.averageDailyProfit30Days || 0);
    refs.projectedWeeklyProfit.textContent = formatMoney(summary.projectedWeeklyProfit || 0);
    refs.lowStockCount.textContent = String(summary.lowStockAlerts?.length || 0);
    renderCategoryCounts(summary.categoryCounts || []);
    renderLowStock(summary.lowStockAlerts || []);
}

function renderCategoryCounts(items) {
    refs.categoryCounts.innerHTML = "";
    mountEmpty(refs.categoryCounts, items.length === 0);
    items.forEach((item) => {
        refs.categoryCounts.appendChild(node(`
            <div class="category-item">
                <strong>${prettyEnum(item.category)}</strong>
                <span>${item.totalQuantity}</span>
            </div>
        `));
    });
}

function renderLowStock(items) {
    refs.lowStockAlerts.innerHTML = "";
    mountEmpty(refs.lowStockAlerts, items.length === 0, "No low-stock alerts", "Thresholds look healthy right now.");
    items.forEach((item) => {
        refs.lowStockAlerts.appendChild(node(`
            <div class="feed-item">
                <strong>${item.productName} · ${item.size} / ${item.color}</strong>
                <p>SKU ${item.sku} is at <b>${item.quantityInStock}</b> units, against a threshold of ${item.threshold}.</p>
            </div>
        `));
    });
}

function renderProducts(items) {
    refs.productsGrid.innerHTML = "";
    mountEmpty(refs.productsGrid, items.length === 0);
    items.forEach((product) => {
        const variants = (product.variants || []).map((variant) => `
            <div class="variant-item">
                <strong>${variant.size} / ${variant.color}</strong>
                <div class="card-meta">Stock ${variant.quantityInStock} · Cost ${formatMoney(variant.unitCost)} · Retail ${formatMoney(variant.retailPrice)}</div>
            </div>
        `).join("");

        refs.productsGrid.appendChild(node(`
            <article class="product-card">
                <div class="card-top">
                    <div>
                        <strong>${product.name}</strong>
                        <div class="card-meta">${prettyEnum(product.category)} · ${product.sku}</div>
                    </div>
                    <span class="badge">${formatMoney(product.totalStockValue)}</span>
                </div>
                <div class="variant-list">${variants || emptyInline("No variants available.")}</div>
            </article>
        `));
    });
}

function renderPaints(items) {
    refs.paintGrid.innerHTML = "";
    mountEmpty(refs.paintGrid, items.length === 0);
    items.forEach((paint) => {
        refs.paintGrid.appendChild(node(`
            <article class="product-card">
                <div class="card-top">
                    <div>
                        <strong>${paint.name}</strong>
                        <div class="card-meta">${paint.paintType} · ${paint.color}</div>
                    </div>
                    <span class="badge">${paint.lowStock ? "Low" : "Healthy"}</span>
                </div>
                <div class="variant-item">
                    <strong>${paint.quantityAvailable} ${paint.unit}</strong>
                    <div class="card-meta">Cost ${formatMoney(paint.costPerUnit)} · Threshold ${paint.lowStockThreshold}</div>
                </div>
            </article>
        `));
    });
}

function renderJobs(items) {
    refs.jobsGrid.innerHTML = "";
    mountEmpty(refs.jobsGrid, items.length === 0);
    items.forEach((job) => {
        refs.jobsGrid.appendChild(node(`
            <article class="product-card">
                <div class="card-top">
                    <div>
                        <strong>${job.productName}</strong>
                        <div class="card-meta">${job.variantLabel || "No variant"} · ${job.status}</div>
                    </div>
                    <span class="badge">${job.quantityPrinted} pcs</span>
                </div>
                <div class="variant-list">
                    <div class="variant-item">
                        <strong>Production cost ${formatMoney(job.productionCost)}</strong>
                        <div class="card-meta">Retail ${formatMoney(job.retailValue)} · Profit ${formatMoney(job.estimatedProfit)}</div>
                    </div>
                </div>
            </article>
        `));
    });
}

function renderStockHistory(items) {
    refs.stockHistory.innerHTML = "";
    mountEmpty(refs.stockHistory, items.length === 0, "No recent movements", "Stock changes will show up here once you start operating.");
    items.forEach((item) => {
        refs.stockHistory.appendChild(node(`
            <div class="feed-item">
                <strong>${item.productName} · ${item.type}</strong>
                <p>${item.quantity} units, new balance ${item.balanceAfter}. Ref ${item.reference || "-"} by ${item.performedBy}.</p>
            </div>
        `));
    });
}

function renderReport(report, titleRef, summaryRef, itemsRef, formatter) {
    titleRef.textContent = `${report.reportName} · ${report.from} to ${report.to}`;
    summaryRef.innerHTML = `
        <div class="summary-chip">
            <span>Revenue estimate</span>
            <strong>${formatMoney(report.summary.totalRevenueEstimate || 0)}</strong>
        </div>
        <div class="summary-chip">
            <span>Total cost</span>
            <strong>${formatMoney(report.summary.totalCost || 0)}</strong>
        </div>
        <div class="summary-chip">
            <span>Profit margin</span>
            <strong>${Number(report.summary.profitMargin || 0).toFixed(2)}%</strong>
        </div>
    `;
    itemsRef.innerHTML = "";
    mountEmpty(itemsRef, (report.items || []).length === 0, "No report items", "Try a wider date range.");
    (report.items || []).slice(0, 10).forEach((item) => itemsRef.appendChild(formatter(item)));
}

function formatStockReportItem(item) {
    return node(`
        <div class="feed-item">
            <strong>${item.productName} · ${item.variant}</strong>
            <p>${item.transactionType} ${item.quantity} units. Balance ${item.balanceAfter}. Ref ${item.reference || "-"}. Value ${formatMoney(item.stockValueAfter)}</p>
        </div>
    `);
}

function formatJobsReportItem(item) {
    return node(`
        <div class="feed-item">
            <strong>${item.productName} · ${item.variant}</strong>
            <p>${item.quantityPrinted} units · ${item.status}. Cost ${formatMoney(item.productionCost)} and profit ${formatMoney(item.estimatedProfit)}.</p>
        </div>
    `);
}

function populateSelectors() {
    const variantOptions = flattenVariants();
    setSelectOptions(refs.stockVariantId, variantOptions, "Choose a variant");
    setSelectOptions(refs.printJobProductId, state.products.map((product) => ({
        value: product.id,
        label: `${product.name} · ${product.sku}`
    })), "Choose a product");
    setSelectOptions(refs.printJobPaintId, state.paints.map((paint) => ({
        value: paint.id,
        label: `${paint.name} · ${paint.color}`
    })), "Choose a paint");
    syncPrintJobVariantOptions();
}

function syncPrintJobVariantOptions() {
    const productId = Number(refs.printJobProductId.value);
    const product = state.products.find((item) => item.id === productId);
    const variants = (product?.variants || []).map((variant) => ({
        value: variant.id,
        label: `${variant.size} / ${variant.color} · ${variant.quantityInStock} in stock`
    }));
    setSelectOptions(refs.printJobVariantId, variants, "Optional variant", true);
}

function flattenVariants() {
    return state.products.flatMap((product) =>
        (product.variants || []).map((variant) => ({
            value: variant.id,
            label: `${product.name} · ${variant.size} / ${variant.color} · ${variant.quantityInStock} in stock`
        }))
    );
}

function setSelectOptions(select, options, placeholder, allowEmpty = false) {
    if (!select) {
        return;
    }
    const firstOption = allowEmpty
            ? `<option value="">${placeholder}</option>`
            : `<option value="" disabled selected>${placeholder}</option>`;
    select.innerHTML = firstOption + options.map((option) =>
        `<option value="${option.value}">${option.label}</option>`
    ).join("");
    if (options.length && !allowEmpty) {
        select.value = String(options[0].value);
    }
}

function clearWorkspace() {
    state.products = [];
    state.paints = [];
    state.printJobs = [];
    state.stockHistory = [];

    renderSummary({
        totalStockValue: 0,
        totalStockCost: 0,
        estimatedProfitPotential: 0,
        completedJobsRevenue: 0,
        completedJobsProfit: 0,
        averageDailyProfit30Days: 0,
        projectedWeeklyProfit: 0,
        categoryCounts: [],
        lowStockAlerts: []
    });
    renderProducts([]);
    renderPaints([]);
    renderJobs([]);
    renderStockHistory([]);
    renderReport({
        reportName: "Stock Report",
        from: refs.reportFrom.value,
        to: refs.reportTo.value,
        summary: { totalRevenueEstimate: 0, totalCost: 0, profitMargin: 0 },
        items: []
    }, refs.stockReportTitle, refs.stockReportSummary, refs.stockReportItems, formatStockReportItem);
    renderReport({
        reportName: "Print Job Report",
        from: refs.reportFrom.value,
        to: refs.reportTo.value,
        summary: { totalRevenueEstimate: 0, totalCost: 0, profitMargin: 0 },
        items: []
    }, refs.jobsReportTitle, refs.jobsReportSummary, refs.jobsReportItems, formatJobsReportItem);
    populateSelectors();
}

function expireSession() {
    state.token = "";
    state.email = "";
    state.role = "Guest";
    persistSession();
    hydrateAuthUi();
    clearWorkspace();
    activateAuthView("login");
    setShellVisibility(false);
}

function mountEmpty(container, shouldMount, title = "No data yet", message = "Start seeding inventory to populate this view.") {
    if (!shouldMount) {
        return;
    }
    container.appendChild(node(`
        <div class="empty-state">
            <strong>${title}</strong>
            <p>${message}</p>
        </div>
    `));
}

function ensureSignedIn() {
    if (state.token) {
        return true;
    }
    showToast("Sign in required", "Please authenticate before making changes.", "error");
    return false;
}

function setDefaultDates() {
    const today = new Date();
    const from = new Date();
    from.setDate(today.getDate() - 30);
    refs.reportTo.value = today.toISOString().slice(0, 10);
    refs.reportFrom.value = from.toISOString().slice(0, 10);
}

function settledValue(result, fallback, label) {
    if (result.status === "fulfilled") {
        return result.value;
    }
    showToast("Partial data unavailable", `Could not load ${label}. ${normalizeErrorMessage(result.reason?.message || "Unknown error")}`, "error");
    return fallback;
}

function showToast(title, message, type = "success") {
    const toast = node(`
        <div class="toast ${type}">
            <strong>${title}</strong>
            <p>${message}</p>
        </div>
    `);
    refs.toastHost.appendChild(toast);
    setTimeout(() => {
        toast.remove();
    }, 4500);
}

function normalizeErrorMessage(message) {
    if (!message) {
        return "Something went wrong.";
    }
    if (message.includes("JDBC exception") || message.includes("could not determine data type")) {
        return "The server hit a database query problem. Refresh and try again.";
    }
    if (message.includes("Failed to fetch")) {
        return "The app could not reach the backend. Confirm Docker is running and the API URL is correct.";
    }
    return message.length > 180 ? `${message.slice(0, 177)}...` : message;
}

function cleanBaseUrl(value) {
    return (value.trim() || window.location.origin).replace(/\/$/, "");
}

function prettyEnum(value) {
    return String(value || "").replaceAll("_", " ").toLowerCase().replace(/\b\w/g, (char) => char.toUpperCase());
}

function formatMoney(value) {
    return new Intl.NumberFormat("en-KE", {
        style: "currency",
        currency: "KES",
        maximumFractionDigits: 2
    }).format(Number(value || 0));
}

function toInt(value) {
    return Number.parseInt(value, 10);
}

function toNumber(value) {
    return Number.parseFloat(value);
}

function node(markup) {
    const template = document.createElement("template");
    template.innerHTML = markup.trim();
    return template.content.firstElementChild;
}

function emptyInline(message) {
    return `<div class="variant-item"><div class="card-meta">${message}</div></div>`;
}

init();

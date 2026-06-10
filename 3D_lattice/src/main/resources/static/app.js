class Dashboard {
    constructor() {
        this.charts = {
            energy: null,
            kinetic: null,
            temp: null,
            bar: null
        };
        this.init();
    }

    async init() {
        this.setupEventListeners();
        await this.loadExperiments();
    }

    setupEventListeners() {
        const refreshBtn = document.getElementById('btn-refresh');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => {
                refreshBtn.style.opacity = '0.5';
                this.loadExperiments().finally(() => refreshBtn.style.opacity = '1');
            });
        }
    }

    async loadExperiments() {
        try {
            const res = await fetch('/api/experiments');
            if (!res.ok) throw new Error("Помилка завантаження списку");
            const data = await res.json();
            this.renderTable(data);
        } catch (err) {
            console.error('Failed to load experiments:', err);
            const tbody = document.getElementById('table-body');
            tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;
            color:var(--danger-color)">Помилка з'єднання</td></tr>`;
        }
    }

    renderTable(data) {
        const tbody = document.getElementById('table-body');
        tbody.innerHTML = '';
        
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4"' +
                'style="text-align: center;' +
                'color: var(--text-secondary);' +
                'padding: 2rem;">Немає експериментів</td></tr>';
            return;
        }

        // Sort descending by ID so newest is on top
        data.sort((a, b) => b.id - a.id).forEach(exp => {
            const tr = document.createElement('tr');
            
            // Format date nicely
            const dateObj = new Date(exp.createdAt);
            const dateStr = dateObj.toLocaleDateString(
                'uk-UA', { day: '2-digit', month: '2-digit' });
            const timeStr = dateObj.toLocaleTimeString(
                'uk-UA', { hour: '2-digit', minute:'2-digit', second:'2-digit' });
            
            tr.innerHTML = `
                <td style="color:var(--text-secondary)">#${exp.id}</td>
                <td><strong style="color:var(--text-primary)">${exp.materialName}</strong></td>
                <td><span
                style="background:rgba(0,0,0,0.05);
                padding:4px 8px;
                border-radius:4px;
                color:var(--text-primary);
                font-size:0.75rem">${exp.algorithmType}</span></td>
                <td><span style="color:var(--text-secondary)">${timeStr}</span>
                <span style="opacity:0.5;font-size:0.75rem">${dateStr}</span></td>
            `;
            
            tr.onclick = () => {
                document.querySelectorAll(
                    '#table-body tr')
                    .forEach(row => row
                        .classList
                        .remove('active'));
                tr.classList.add('active');
                this.loadExperimentDetails(exp.id, exp.materialName, exp.algorithmType);
            };
            
            tbody.appendChild(tr);
        });
    }

    async loadExperimentDetails(id, material, algorithm) {
        const titleEl = document.getElementById('chart-title');
        const containerEl = document.getElementById('charts-container');
        const emptyStateEl = document.getElementById('empty-state');
        
        try {
            const res = await fetch(`/api/experiments/${id}`);
            if (!res.ok) throw new Error("Помилка завантаження точок");
            const exp = await res.json();
            
            const points = exp.dataPoints || [];
            if (points.length === 0) {
                alert("Цей експеримент не містить даних.");
                return;
            }

            titleEl.innerHTML = `
                Експеримент #${id} 
                <span
                style="background:rgba(59,130,246,0.1);
                color:#2563eb;font-size:0.875rem;
                padding:4px 10px;
                border-radius:12px;
                margin-left:15px;
                font-weight:500;">
                    ${material} &middot; ${algorithm}
                </span>
            `;
            
            if (emptyStateEl) emptyStateEl.style.display = 'none';
            containerEl.style.display = 'flex';
            
            this.renderCharts(points);
        } catch (err) {
            console.error(err);
            alert("Помилка: " + err.message);
        }
    }

    renderCharts(points) {
        const labels = points.map(p => p.step);
        const kE = points.map(p => p.kineticEnergy);
        const pE = points.map(p => p.potentialEnergy);
        const tE = points.map(p => p.totalEnergy);
        const temp = points.map(p => p.temperature);

        this.destroyCharts();

        // Chart.js Global Defaults
        Chart.defaults.color = '#64748b';
        Chart.defaults.font.family = "'Inter', sans-serif";

        const gridOptions = {
            color: 'rgba(0, 0, 0, 0.05)',
            tickColor: 'transparent'
        };

        const commonOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { 
                    position: 'top',
                    labels: { usePointStyle: true, boxWidth: 8, padding: 20, font: {size: 13} } 
                },
                tooltip: {
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    titleColor: '#0f172a',
                    bodyColor: '#334155',
                    titleFont: { size: 14, family: 'Inter' },
                    bodyFont: { size: 13, family: 'Inter' },
                    padding: 12,
                    cornerRadius: 8,
                    borderColor: 'rgba(0,0,0,0.1)',
                    borderWidth: 1
                }
            },
            scales: {
                x: { grid: gridOptions, border: {display: false} },
                y: { grid: gridOptions, border: {display: false} }
            },
            interaction: { mode: 'index', intersect: false }
        };

        // Energy Line Chart
        const ctxE = document.getElementById('energyChart').getContext('2d');
        this.charts.energy = new Chart(ctxE, {
            type: 'line',
            data: {
                labels,
                datasets: [
                    {
                        label: 'Потенціальна',
                        data: pE, borderColor: '#3b82f6',
                        borderWidth: 2, pointRadius: 0, tension: 0.4
                    },
                    {
                        label: 'Повна Енергія',
                        data: tE, borderColor: '#10b981',
                        borderWidth: 2, pointRadius: 0, tension: 0.4
                    }
                ]
            },
            options: { ...commonOptions }
        });

        // Kinetic Line Chart
        const ctxK = document.getElementById('kineticChart').getContext('2d');
        const gradientK = ctxK.createLinearGradient(0, 0, 0, 300);
        gradientK.addColorStop(0, 'rgba(244, 63, 94, 0.2)');
        gradientK.addColorStop(1, 'rgba(244, 63, 94, 0)');
        
        this.charts.kinetic = new Chart(ctxK, {
            type: 'line',
            data: {
                labels,
                datasets: [{
                    label: 'Кінетична',
                    data: kE, borderColor: '#f43f5e',
                    backgroundColor: gradientK,
                    borderWidth: 2, pointRadius: 0, fill: true, tension: 0.4
                }]
            },
            options: { ...commonOptions }
        });

        // Temperature Line Chart
        const ctxT = document.getElementById('tempChart').getContext('2d');
        
        // Gradient for Temperature
        const gradientT = ctxT.createLinearGradient(0, 0, 0, 300);
        gradientT.addColorStop(0, 'rgba(245, 158, 11, 0.2)');
        gradientT.addColorStop(1, 'rgba(245, 158, 11, 0)');

        this.charts.temp = new Chart(ctxT, {
            type: 'line',
            data: {
                labels,
                datasets: [{ 
                    label: 'Температура (K)', 
                    data: temp, 
                    borderColor: '#f59e0b',
                    backgroundColor: gradientT,
                    borderWidth: 2, 
                    pointRadius: 0, 
                    fill: true,
                    tension: 0.4 
                }]
            },
            options: { ...commonOptions }
        });

        // Averages Bar Chart
        const avgK = kE.reduce((a, b) => a + b, 0) / kE.length;
        const avgP = pE.reduce((a, b) => a + b, 0) / pE.length;
        const avgT = tE.reduce((a, b) => a + b, 0) / tE.length;

        const ctxB = document.getElementById('barChart').getContext('2d');
        this.charts.bar = new Chart(ctxB, {
            type: 'bar',
            data: {
                labels: ['Кінетична', 'Потенціальна', 'Повна'],
                datasets: [{
                    label: 'Середнє значення',
                    data: [avgK, avgP, avgT],
                    backgroundColor: [
                        'rgba(244, 63, 94, 0.8)',
                        'rgba(59, 130, 246, 0.8)',
                        'rgba(16, 185, 129, 0.8)'
                    ],
                    borderRadius: 6,
                    barThickness: 50
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { 
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: 'rgba(255, 255, 255, 0.95)',
                        titleColor: '#0f172a',
                        bodyColor: '#334155',
                        borderColor: 'rgba(0,0,0,0.1)',
                        borderWidth: 1,
                        padding: 12,
                        cornerRadius: 8
                    }
                },
                scales: {
                    x: { grid: { display: false }, border: {display: false} },
                    y: { grid: gridOptions, border: {display: false} }
                }
            }
        });
    }

    destroyCharts() {
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
    }
}

// Start application
document.addEventListener("DOMContentLoaded", () => {
    window.appDashboard = new Dashboard();
});

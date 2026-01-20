const express = require('express');
const sql = require('mssql');
const app = express();
const port = 3000;

// Configure the SQL Server connection
const config = {
    server: 'DESKTOP-C510AIL\\SQLEXPRESS', // Your server name and instance
    database: 'TemperatureMonitor',
    options: {
        encrypt: true, // Use encryption if needed
        trustServerCertificate: true // For local dev, you might need to set this to true
    },
    authentication: {
        type: 'ntlm',
        options: {
            domain: '', // Set to your domain if necessary
            userName: '', // Leave empty for Windows Authentication
            password: '' // Leave empty for Windows Authentication
        }
    }
};

// Connect to SQL Server
sql.connect(config)
    .then(pool => {
        if (pool.connected) console.log('Connected to SQL Server');
    })
    .catch(err => console.error('SQL Server connection error', err));

// Define API endpoint to fetch temperature data
app.get('/api/temperatures', async (req, res) => {
    try {
        const pool = await sql.connect(config);
        const result = await pool.request().query('SELECT * FROM RoomTemperatures');
        res.json(result.recordset); // Send data in JSON format
    } catch (err) {
        res.status(500).send('Server Error');
        console.error('Database query error', err);
    }
});

// Start the server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}/`);
});

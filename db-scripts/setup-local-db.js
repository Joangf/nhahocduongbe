const { Client } = require('pg');

// Local PostgreSQL via Docker on port 5433
const localConfig = {
  host: 'localhost',
  port: 5433,
  database: 'postgres',
  user: 'postgres',
  password: 'local123'
};

// Supabase connection
const supabaseConfig = {
  host: 'aws-1-ap-northeast-1.pooler.supabase.com',
  port: 6543,
  database: 'postgres',
  user: 'postgres.pcddwfbyigcdhbhlzwer',
  password: 'mqXYkJ63v8Tlkpju',
  ssl: { rejectUnauthorized: false }
};

const action = process.argv[2] || 'setup';
const client = new Client(action === 'supabase' ? supabaseConfig : localConfig);

client.connect()
  .then(() => {
    if (action === 'setup') {
      console.log('Connected to LOCAL PostgreSQL on port 5433');
      return client.query('CREATE SCHEMA IF NOT EXISTS nhahocduong')
        .then(() => client.query('CREATE SCHEMA IF NOT EXISTS public'))
        .then(() => console.log('Schemas created: nhahocduong, public'));
    } else if (action === 'supabase') {
      console.log('Connected to SUPABASE');
      return client.query("SELECT table_name FROM information_schema.tables WHERE table_schema='nhahocduong' ORDER BY table_name")
        .then(res => {
          console.log(`Found ${res.rows.length} tables in nhahocduong:`);
          res.rows.forEach(r => console.log('  -', r.table_name));
        });
    }
  })
  .then(() => client.end())
  .catch(e => { console.log('ERROR:', e.message); process.exit(1); });

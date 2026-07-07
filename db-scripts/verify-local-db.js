const { Client } = require('pg');
const client = new Client({
  host: 'localhost', port: 5433, database: 'postgres',
  user: 'postgres', password: 'local123'
});

client.connect()
  .then(() => client.query(`
    SELECT table_name,
           (SELECT COUNT(*) FROM information_schema.tables t2
            WHERE t2.table_schema = 'nhahocduong') as total_tables
    FROM information_schema.tables
    WHERE table_schema = 'nhahocduong'
    ORDER BY table_name
  `))
  .then(res => {
    console.log(`Found ${res.rows.length} tables in nhahocduong schema:\n`);
    res.rows.forEach(r => console.log('  -', r.table_name));
    return client.query(`SELECT COUNT(*) as cnt FROM nhahocduong.nhahocduong_patient`);
  })
  .then(res => console.log(`\nPatients: ${res.rows[0].cnt}`))
  .then(() => client.query(`SELECT COUNT(*) as cnt FROM nhahocduong.nhahocduong_exam`))
  .then(res => console.log(`Exams: ${res.rows[0].cnt}`))
  .then(() => client.query(`SELECT COUNT(*) as cnt FROM nhahocduong.nhahocduong_organization`))
  .then(res => console.log(`Organizations: ${res.rows[0].cnt}`))
  .then(() => client.end())
  .then(() => console.log('\nVerification complete!'))
  .catch(e => { console.log('ERROR:', e.message); process.exit(1); });

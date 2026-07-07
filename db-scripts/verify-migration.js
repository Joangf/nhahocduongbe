const { Client } = require('pg');
const client = new Client({
  host: 'localhost', port: 5433, database: 'postgres',
  user: 'postgres', password: 'local123'
});

async function verify() {
  await client.connect();
  console.log('=== VERIFY MIGRATION RESULTS ===\n');

  // 1. Academic Year
  const ay = await client.query(
    'SELECT id, name, status, start_date, end_date FROM nhahocduong.academic_year ORDER BY id'
  );
  console.log('1. ACADEMIC_YEAR:');
  ay.rows.forEach(r => console.log(`   [${r.id}] ${r.name} | status=${r.status} | ${r.start_date} → ${r.end_date}`));

  // 2. Classes
  const cls = await client.query(
    'SELECT COUNT(*) as cnt, COUNT(DISTINCT school_id) as schools FROM nhahocduong.class'
  );
  console.log(`\n2. CLASS: ${cls.rows[0].cnt} lớp, ${cls.rows[0].schools} trường`);

  // Show sample
  const clsSample = await client.query(
    'SELECT c.id, c.name, c.grade, c.room, o.name as school FROM nhahocduong.class c JOIN nhahocduong.nhahocduong_organization o ON o.id = c.school_id LIMIT 10'
  );
  clsSample.rows.forEach(r => console.log(`   [${r.id}] ${r.name} (grade=${r.grade}, room=${r.room}) - ${r.school}`));

  // 3. Student affiliations
  const aff = await client.query(
    'SELECT COUNT(*) as cnt FROM nhahocduong.student_class_affiliation'
  );
  console.log(`\n3. STUDENT_CLASS_AFFILIATION: ${aff.rows[0].cnt} bản ghi`);

  const affSample = await client.query(`
    SELECT a.id, p.full_name, c.name as class, ay.name as year, a.status
    FROM nhahocduong.student_class_affiliation a
    JOIN nhahocduong.nhahocduong_patient p ON p.id = a.student_id
    JOIN nhahocduong.class c ON c.id = a.class_id
    JOIN nhahocduong.academic_year ay ON ay.id = a.academic_year_id
    LIMIT 10
  `);
  affSample.rows.forEach(r => console.log(`   ${r.full_name} → ${r.class} (${r.year}) [${r.status}]`));

  // 4. Exam academic_year_id
  const exam = await client.query(`
    SELECT COUNT(*) as cnt,
           COUNT(academic_year_id) as has_ay_id
    FROM nhahocduong.nhahocduong_exam
  `);
  console.log(`\n4. EXAM: ${exam.rows[0].has_ay_id}/${exam.rows[0].cnt} có academic_year_id`);

  // 5. System log
  const log = await client.query('SELECT COUNT(*) as cnt FROM nhahocduong.system_log');
  console.log(`\n5. SYSTEM_LOG: ${log.rows[0].cnt} bản ghi (sẵn sàng)`);

  // 6. Check constraints
  const cons = await client.query(`
    SELECT conname, contype
    FROM pg_constraint
    WHERE conrelid IN (
      'nhahocduong.academic_year'::regclass,
      'nhahocduong.class'::regclass,
      'nhahocduong.student_class_affiliation'::regclass,
      'nhahocduong.system_log'::regclass
    )
    ORDER BY conname
  `);
  console.log('\n6. CONSTRAINTS:');
  cons.rows.forEach(r => console.log(`   ${r.conname} (type=${r.contype})`));

  // 7. Indexes
  const idx = await client.query(`
    SELECT indexname FROM pg_indexes
    WHERE schemaname = 'nhahocduong'
      AND tablename IN ('academic_year', 'class', 'student_class_affiliation', 'system_log')
    ORDER BY indexname
  `);
  console.log('\n7. INDEXES:');
  idx.rows.forEach(r => console.log(`   ${r.indexname}`));

  await client.end();
  console.log('\n=== VERIFY COMPLETE ===');
}

verify().catch(e => { console.log('ERROR:', e.message); process.exit(1); });

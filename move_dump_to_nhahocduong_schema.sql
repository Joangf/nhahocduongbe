CREATE SCHEMA IF NOT EXISTS nhahocduong;

DO $$
DECLARE
  item record;
BEGIN
  FOR item IN
    SELECT tablename AS name
    FROM pg_tables
    WHERE schemaname = 'public'
  LOOP
    EXECUTE format('ALTER TABLE public.%I SET SCHEMA nhahocduong', item.name);
  END LOOP;

  FOR item IN
    SELECT sequencename AS name
    FROM pg_sequences
    WHERE schemaname = 'public'
  LOOP
    EXECUTE format('ALTER SEQUENCE public.%I SET SCHEMA nhahocduong', item.name);
  END LOOP;

  FOR item IN
    SELECT table_name AS name
    FROM information_schema.views
    WHERE table_schema = 'public'
  LOOP
    EXECUTE format('ALTER VIEW public.%I SET SCHEMA nhahocduong', item.name);
  END LOOP;
END $$;

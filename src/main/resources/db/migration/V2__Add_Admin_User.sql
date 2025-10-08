INSERT INTO users (email, password, role)
SELECT '${admin.email}', '${admin.password}', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT email FROM users WHERE email = '${admin.email}');
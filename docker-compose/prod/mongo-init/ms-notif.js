db = db.getSiblingDB('msnotifdb');
db.createUser({
  user: 'msnotifier',
  pwd: 'msnotifierpwd',
  roles: [{ role: 'readWrite', db: 'msnotifdb' }]
});

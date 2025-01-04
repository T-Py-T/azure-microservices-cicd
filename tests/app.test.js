const app = require('../index'); // Adjust the path to your main application file

test('Application context loads without errors', () => {
  expect(app).toBeDefined();
});
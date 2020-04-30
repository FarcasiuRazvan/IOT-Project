module.exports = (app) => {
    const noises = require('../controllers/noise.controller.js');

    // Create a new Noise model
    app.post('/noises', noises.create);

    // Retrieve all Noise models
    app.get('/noises', noises.findAll);

    // Retrieve a single Noise model with noiseId
    app.get('/noises/:noiseId', noises.findOne);

    // Update a Noise model with noiseId
    app.put('/noises/:noiseId', noises.update);

    // Delete a Noise model with noiseId
    app.delete('/noises/:noiseId', noises.delete);
}
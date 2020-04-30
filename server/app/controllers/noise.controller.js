const Noise = require('../models/noise.model.js');

// Create and Save a new Noise
exports.create = (req, res) => {
    // Validate request
    if (!req.body.number) {
        return res.status(400).send({
            message: "Noise value can not be null"
        });
    }

    // Create a Noise
    const noise = new Noise({
        title: req.body.title || "Untitled Noise",
        number: req.body.number
    });

    // Save Noise in the database
    noise.save()
        .then(data => {
            res.send(data);
        }).catch(err => {
            res.status(500).send({
                message: err.message || "Some error occurred while creating the Noise."
            });
        });
};

// Retrieve and return all Noises from the database.
exports.findAll = (req, res) => {
    Noise.find()
        .then(noises => {
            res.send(noises);
        }).catch(err => {
            res.status(500).send({
                message: err.message || "Some error occurred while retrieving noises."
            });
        });
};

// Find a single Noise with a noiseId
exports.findOne = (req, res) => {
    Noise.findById(req.params.noiseId)
        .then(noise => {
            if (!noise) {
                return res.status(404).send({
                    message: "Noise not found with id " + req.params.noiseId
                });
            }
            res.send(noise);
        }).catch(err => {
            if (err.kind === 'ObjectId') {
                return res.status(404).send({
                    message: "Noise not found with id " + req.params.noiseId
                });
            }
            return res.status(500).send({
                message: "Error retrieving noise with id " + req.params.noiseId
            });
        });
};

// Update a Noise identified by the noiseId in the request
exports.update = (req, res) => {
    // Validate Request
    if (!req.body.number) {
        return res.status(400).send({
            message: "Noise value can not be null"
        });
    }

    // Find noise and update it with the request body
    Noise.findByIdAndUpdate(req.params.noiseId, {
        title: req.body.title || "Untitled Noise",
        number: req.body.number
    }, { new: true })
        .then(noise => {
            if (!noise) {
                return res.status(404).send({
                    message: "Noise not found with id " + req.params.noiseId
                });
            }
            res.send(noise);
        }).catch(err => {
            if (err.kind === 'ObjectId') {
                return res.status(404).send({
                    message: "Noise not found with id " + req.params.noiseId
                });
            }
            return res.status(500).send({
                message: "Error updating noise with id " + req.params.noiseId
            });
        });
};

// Delete a Noise with the specified noiseId in the request
exports.delete = (req, res) => {
    Noise.findByIdAndRemove(req.params.noiseId)
        .then(noise => {
            if (!noise) {
                return res.status(404).send({
                    message: "Noise not found with id " + req.params.noiseId
                });
            }
            res.send({ message: "Noise deleted successfully!" });
        }).catch(err => {
            if (err.kind === 'ObjectId' || err.name === 'NotFound') {
                return res.status(404).send({
                    message: "Noise not found with id " + req.params.noiseId
                });
            }
            return res.status(500).send({
                message: "Could not delete noise with id " + req.params.noiseId
            });
        });
};
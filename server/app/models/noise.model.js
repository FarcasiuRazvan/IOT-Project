const mongoose = require('mongoose');

const NoiseSchema = mongoose.Schema({
    title: String,
    number: Number
}, {
    timestamps: true
});

module.exports = mongoose.model('Noise', NoiseSchema);
<!DOCTYPE html>
<html>
  <head>
    <title>Simple Map</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
    <style>
      #map {
        height: 500px;
        width: 100%;
      }
    </style>
  </head>
  <body>
    <div id="map"></div>
    <script>
      var map = L.map('map').setView([28.61861351908397, 77.37819628046492], 13); // Replace with your coordinates

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(map);

      var marker = L.marker([28.61861351908397, 77.37819628046492]).addTo(map)  // Replace with your coordinates
        .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
        .openPopup();
    </script>
  </body>
</html>
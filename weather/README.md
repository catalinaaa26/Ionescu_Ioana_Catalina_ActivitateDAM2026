# Modulul AccuWeather

Copiază \`secrets.properties.example\` în fișierul \`secrets.properties\` din rădăcina proiectului și înlocuiește \`YOUR_API_KEY_HERE\` cu cheia ta AccuWeather. Fișierul real este exclus din Git. Reîncarcă proiectul Gradle după salvare.

Modulul folosește cheia în antetul \`Authorization: Bearer\`, caută orașe prin Locations API și afișează prognoza zilnică pe 1 sau 5 zile.
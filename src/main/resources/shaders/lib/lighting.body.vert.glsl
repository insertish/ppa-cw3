fragPosition = vec3(model * vec4(vertexPos, 1.0));
fragNormal = mat3(transpose(inverse(model))) * vertexNormal;

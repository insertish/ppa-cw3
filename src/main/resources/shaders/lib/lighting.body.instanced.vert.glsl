fragPosition = vec3(model[gl_InstanceID] * vec4(vertexPos, 1.0));
fragNormal = mat3(transpose(inverse(model[gl_InstanceID]))) * vertexNormal;

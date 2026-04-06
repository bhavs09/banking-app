# Use lightweight Nginx image
FROM nginx:alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy our custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy HTML files to nginx serve directory
COPY html/ /usr/share/nginx/html/

# Expose port 80
EXPOSE 80

# Nginx runs automatically
CMD ["nginx", "-g", "daemon off;"]
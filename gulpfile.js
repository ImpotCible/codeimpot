'use strict';

var gulp = require('gulp')
var sass = require('gulp-sass');

gulp.task('sass', function () {
  return gulp.src('./assets/sass/default.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest('./src/main/webapp/css'))
});

gulp.task('index', function () {
	  return gulp.src('./src/main/webapp/index.html')
	    .pipe(gulp.dest('d:/Profiles/vnhim/server/apache-tomcat-8.0.32/webapps/codeimpots-0.0.1-SNAPSHOT'));
	});

gulp.task('script', function () {
	  return gulp.src('./src/main/webapp/script/*.js')
	    .pipe(gulp.dest('d:/Profiles/vnhim/server/apache-tomcat-8.0.32/webapps/codeimpots-0.0.1-SNAPSHOT/script'));
	});

 
gulp.task('sass:watch', function () {
  gulp.watch('./assets/sass/**/*.scss', ['sass']);
});

gulp.task('index:watch', function () {
	  gulp.watch('./src/main/webapp/index.html', ['index']);
	});
gulp.task('script:watch', function () {
	  gulp.watch('./src/main/webapp/script/*.js', ['script']);
	});

gulp.task('default', ['sass', 'index', 'sass:watch', 'index:watch', 'script:watch'])
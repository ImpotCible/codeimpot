'use strict';

var gulp = require('gulp')
var sass = require('gulp-sass');

gulp.task('sass', function () {
  return gulp.src('./assets/sass/default.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest('./src/main/webapp/css'))
    .pipe(gulp.dest('d:/Profiles/vnhim/server/apache-tomcat-8.0.32/webapps/codeimpots-0.0.1-SNAPSHOT/css'));
});

 
gulp.task('sass:watch', function () {
  gulp.watch('./assets/sass/**/*.scss', ['sass']);
});


	});

gulp.task('default', ['sass', 'sass:watch'])
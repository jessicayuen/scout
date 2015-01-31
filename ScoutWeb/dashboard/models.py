from django.db import models
from django.contrib.auth.models import User

# Note that django models already include an id, and that Django User models
# include username/password and optionally email, first_name and last_name. See
# https://docs.djangoproject.com/en/1.7/ref/contrib/auth/ for more info.


class Customer(models.Model):
    user = models.OneToOneField(User)
    points = models.IntegerField()


class Business(models.Model):
    user = models.OneToOneField(User)
    customers = models.ManyToManyField(Customer)


class Rewards(models.Model):
    business = models.ForeignKey(Business)
    points = models.IntegerField()
    reward = models.TextField()


class BLE(models.Model):
    customer = models.ForeignKey(Customer)
    time = models.DateTimeField()

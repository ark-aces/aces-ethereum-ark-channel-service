Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.provider "virtualbox" do |v|
    v.memory = 4048
    v.cpus = 2
  end
  
  # postgreql
  config.vm.network "forwarded_port", guest: 5432, host: 5432
  
  # bitcoind rcp
  config.vm.network "forwarded_port", guest: 18080, host: 18080
  
  # ark node api
  config.vm.network "forwarded_port", guest: 4001, host: 4001
end

package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.repository.UsrRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsrServiceImpl implements UsrService {

    private UsrRepo usrRepo;

    @Autowired
    public UsrServiceImpl(UsrRepo usrRepo) {
        super();
        this.usrRepo = usrRepo;
    }


    @Override
    public Usr getUsr(Long id) {
        return usrRepo.getOne(id);
    }

    @Override
    public Usr getUsrByUsername(String username) {
        return usrRepo.getUsrByUsername(username);
    }

    @Override
    public List<Usr> getAllUsr() {
        return usrRepo.findAll();
    }

    @Override
    public long countUsr() {
        return usrRepo.count();
    }

    @Override
    public Usr saveUsr(Usr Usr) {
        return usrRepo.save(Usr);
    }

}
